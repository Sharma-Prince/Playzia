import json
import requests
from django.contrib.auth import login as django_login, logout as django_logout
from rest_framework.authentication import TokenAuthentication, SessionAuthentication, BasicAuthentication
from rest_framework.authtoken.models import Token
from rest_framework.permissions import IsAuthenticated
from rest_framework.response import Response
from rest_framework.views import APIView
from rest_framework import status
from Users.models import PubgUser, Event, Wallet, Update, PaytmHistory, Transactions, RequestMoney, PlayerJoin
from api.serializers import EventSerializer, TopPlayerSerializer, PubgUserSerializer, LoginSerializer, \
    ProfileSerializer, UpdateSerializer, TransactionsSerializer, ParticipantsListSerializer
from pythonKit import PaytmChecksum as Checksum

MERCHANT_KEY = "***************"
MID = "***************"


class UpdateAPi(APIView):
    def get(self, request):
        update = Update.objects.all()
        serializer = UpdateSerializer(update, many=True)
        return Response(data={"data": serializer.data})


def getData(orderid, user):
    url = "https://securegw-stage.paytm.in/merchant-status/getTxnStatus"
    headers = {'Content-type': 'application/json'}
    respons_dict = {}
    respons_dict['MID'] = MID
    respons_dict['ORDER_ID'] = orderid
    JsonData = {"MID": MID,
                "ORDERID": orderid,
                "CHECKSUMHASH": Checksum.generateSignature(respons_dict, MERCHANT_KEY)}

    response = requests.post(url, json=JsonData)
    data = json.loads(response.content)
    wallet = Wallet.objects.get(user=user)
    tdid = data['TXNID']
    if data['STATUS'] == 'TXN_SUCCESS' and data['RESPMSG'] == 'Txn Success' and data['RESPCODE'] == '01':
        payment = PaytmHistory.objects.get(orderid=orderid)
        if not payment.transactionid:
            if payment.status == 'pending':
                payment.status = 'success'
                payment.transactionid = tdid
                payment.save()
                value = data['TXNAMOUNT']
                wallet.balance = wallet.balance + float(value)
                wallet.save()
                trans = Transactions()
                trans.user = user
                trans.amount = value
                trans.type = 'credit'
                trans.remark = 'Money added to wallet'
                trans.save()
    return wallet.balance


class WalletApi(APIView):
    authentication_classes = [TokenAuthentication, SessionAuthentication, BasicAuthentication]
    permission_classes = [IsAuthenticated]

    def post(self, request):
        serializer = ProfileSerializer(data=request.data)
        serializer.is_valid(raise_exception=True)
        user = serializer.validated_data["user"]
        orderid = request.data['orderid']
        balance = getData(orderid, user)
        data = {}
        data["status"] = "1"
        data["balance"]: balance

        return Response(data=data)


class CheckSumApi(APIView):
    authentication_classes = [TokenAuthentication, SessionAuthentication, BasicAuthentication]
    permission_classes = [IsAuthenticated]

    def post(self, request):
        serializer = ProfileSerializer(data=request.data)
        serializer.is_valid(raise_exception=True)
        user = serializer.validated_data["user"]
        amount = request.data['amount']
        payment = PaytmHistory()
        payment.user = user
        payment.txnamount = amount
        payment.save()

        respons_dict = {}
        respons_dict['MID'] = MID
        respons_dict['ORDER_ID'] = payment.orderid
        respons_dict['CUST_ID'] = user.email
        respons_dict['INDUSTRY_TYPE_ID'] = "Retail"
        respons_dict['CHANNEL_ID'] = "WAP"
        respons_dict['TXN_AMOUNT'] = amount
        respons_dict['WEBSITE'] = "WEBSTAGING"
        respons_dict['CALLBACK_URL'] = "https://pguat.paytm.com/paytmchecksum/paytmCallback.jsp"
        checksum = Checksum.generateSignature(respons_dict,MERCHANT_KEY)
        respons_dict['checksum'] = checksum
        return Response(respons_dict)


class UserLogin(APIView):
    def post(self, request):
        serializer = LoginSerializer(data=request.data)
        serializer.is_valid(raise_exception=True)
        user = serializer.validated_data["user"]
        django_login(request, user)
        token, created = Token.objects.get_or_create(user=user)
        puser = PubgUser.objects.get(user=user)
        firstName = puser.firstName
        lastName = puser.lastName
        phone = puser.phone
        wallet = Wallet.objects.get(user=user)
        balance = str(wallet.balance)
        return Response(
            {"status": "1", "token": token.key, "firstName": firstName, "lastName": lastName, "phone": phone,
             "balance": balance})


class UserLogout(APIView):
    authentication_classes = TokenAuthentication

    def post(self, request):
        django_logout(request)
        return Response(status=204)


class UserRegister(APIView):
    def post(self, request):
        serializer = PubgUserSerializer(data=request.data)
        if serializer.is_valid():
            value = serializer.create(validated_data=request.data)
            return Response({"status": value, "msg": "Registration successful"})  # , status=status.HTTP_201_CREATED
        else:
            return Response({"status": "0", "msg": serializer.errors})  # , status=status.HTTP_400_BAD_REQUEST


class EventList(APIView):
    authentication_classes = [TokenAuthentication, SessionAuthentication, BasicAuthentication]
    permission_classes = [IsAuthenticated]

    def post(self, request):
        serializer = ProfileSerializer(data=request.data)
        serializer.is_valid(raise_exception=True)
        user = serializer.validated_data["user"]
        wallet = Wallet.objects.get(user=user)
        balance = str(wallet.balance)
        event = Event.objects.filter(active=True)
        serializer = EventSerializer(event, many=True)
        return Response(data={"data": serializer.data, "balance": balance})


class UserProfile(APIView):
    authentication_classes = [TokenAuthentication, SessionAuthentication, BasicAuthentication]
    permission_classes = [IsAuthenticated]

    def post(self, request):
        serializer = ProfileSerializer(data=request.data)
        serializer.is_valid(raise_exception=True)
        user = serializer.validated_data["user"]
        puser = PubgUser.objects.get(user=user)
        myKills = puser.myKills
        matchesPlayed = puser.matchesPlayed
        amountWon = puser.amountWon
        firstName = puser.firstName
        lastName = puser.lastName
        phone = puser.phone
        wallet = Wallet.objects.get(user=user)
        balance = str(wallet.balance)
        value = {}
        value['status'] = '1'
        value['myKills'] = str(myKills)
        value['matchesPlayed'] = str(matchesPlayed)
        value['amountWon'] = str(amountWon)
        value['firstName'] = str(firstName)
        value['lastName'] = str(lastName)
        value['phone'] = str(phone)
        value['email'] = str(user.email)
        value['balance'] = str(balance)

        return Response(value)


class TopPlayerList(APIView):
    authentication_classes = [TokenAuthentication, SessionAuthentication, BasicAuthentication]
    permission_classes = [IsAuthenticated]

    def get(self, request):
        user = PubgUser.objects.all().order_by('-amountWon')[0:10]
        serializer = TopPlayerSerializer(user, many=True)
        return Response(data={"data": serializer.data})


class TransactionsList(APIView):
    authentication_classes = [TokenAuthentication, SessionAuthentication, BasicAuthentication]
    permission_classes = [IsAuthenticated]

    def post(self, request):
        serializer = ProfileSerializer(data=request.data)
        serializer.is_valid(raise_exception=True)
        user = serializer.validated_data["user"]
        TNS = Transactions.objects.all().filter(user=user).order_by('-date')
        serializer = TransactionsSerializer(TNS, many=True)
        return Response(data={"data": serializer.data})


class RequestMoneyView(APIView):
    authentication_classes = [TokenAuthentication, SessionAuthentication, BasicAuthentication]
    permission_classes = [IsAuthenticated]

    def post(self, request):
        serializer = ProfileSerializer(data=request.data)
        serializer.is_valid(raise_exception=True)
        user = serializer.validated_data["user"]
        amount = float(request.data['amount'])
        phone = request.data['paytm_no']
        walllet = Wallet.objects.get(user=user)
        if walllet.balance >= amount >= 50:
            Req = RequestMoney()
            Req.user = user
            Req.phone = phone
            Req.money = amount
            Req.save()
            walllet.balance = walllet.balance - amount
            walllet.save()
            value = walllet.balance
            return Response(data={"status": "1", "txn": "success", "balance": value})
        else:
            return Response(data={"status": "0", "txn": "failed"})


class PlayerJoinView(APIView):
    authentication_classes = [TokenAuthentication, SessionAuthentication, BasicAuthentication]
    permission_classes = [IsAuthenticated]

    def post(self, request):
        serializer = ProfileSerializer(data=request.data)
        serializer.is_valid(raise_exception=True)
        user = serializer.validated_data["user"]
        matchID = request.data['match_id']
        matchType = request.data['matchType']
        pubgUsername = request.data['pubgUsername']
        event = Event.objects.get(matchID=matchID)
        fee = float(event.entryFree)
        msg = "failed"
        if event.active:
            wallet = Wallet.objects.get(user=user)
            if wallet.balance >= fee and event.totalPeopleJoined <= 100:
                join = PlayerJoin.objects.filter(matchID=event, pubgusername=pubgUsername)
                if not join:
                    event.totalPeopleJoined = event.totalPeopleJoined + 1
                    event.save()
                    join = PlayerJoin()
                    join.user = user
                    join.matchID = event
                    join.pubgusername = pubgUsername
                    join.type = matchType
                    join.save()
                    wallet.balance = wallet.balance - fee
                    wallet.save()
                    trans = Transactions()
                    trans.user = user
                    trans.amount = fee
                    trans.type = 'debit'
                    trans.remark = event.title
                    trans.save()
                    return Response({"status": "1", "msg": "Player Joined Successful"})
                else:
                    msg = "Player already joined"
            else:
                msg = "No spots left"
        return Response({"status": "0", "msg": msg})


class ParticipantsList(APIView):
    authentication_classes = [TokenAuthentication, SessionAuthentication, BasicAuthentication]
    permission_classes = [IsAuthenticated]

    def post(self, request):
        serializer = ProfileSerializer(data=request.data)
        serializer.is_valid(raise_exception=True)
        user = serializer.validated_data["user"]
        try:
            matchID = request.data['match_id']
            event = Event.objects.get(matchID=matchID)
            if event.active:
                joinlist = PlayerJoin.objects.filter(matchID=event)
                serializer = ParticipantsListSerializer(joinlist, many=True)
                return Response(data={"data": serializer.data})
        except:
            return Response(status=status.HTTP_400_BAD_REQUEST)


class MatchDetails(APIView):
    authentication_classes = [TokenAuthentication, SessionAuthentication, BasicAuthentication]
    permission_classes = [IsAuthenticated]

    def post(self, request):
        serializer = ProfileSerializer(data=request.data)
        serializer.is_valid(raise_exception=True)
        user = serializer.validated_data["user"]
        try:
            matchID = request.data['match_id']
            event = Event.objects.get(matchID=matchID)
            if event.active:
                serializer = EventSerializer(event)
                roomID = ""
                passID = ""
                join = PlayerJoin.objects.filter(user=user, matchID=event)
                if join:
                    roomID = event.roomID
                    passID = event.passID
            return Response(data={"data": serializer.data, 'roomID': roomID, 'passID': passID})
        except:
            return Response(status=status.HTTP_400_BAD_REQUEST)


"""
    Creates the user.
    {"user":{"username":"princdde","password":"fdf#2342","email":"hde@gmail.com"},"firstName":"prince","lastName":"sharma","phone":"1234567890"}
"""
