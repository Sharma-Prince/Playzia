from rest_framework import serializers
from rest_framework.validators import UniqueValidator
from Users.models import PubgUser, Wallet, Event, Update, Transactions, PlayerJoin
from django.contrib.auth.models import User
from rest_framework import exceptions
from rest_framework.authtoken.models import Token
from django.contrib.auth import authenticate


class ProfileSerializer(serializers.Serializer):
    username = serializers.CharField()
    token = serializers.CharField()

    def validate(self, data):
        username = data.get("username", "")
        token = data.get("token", "")

        if username:
            user = User.objects.get(username=username)
            key = Token.objects.get(user=user).key
            if key == token:
                data["user"] = user
                return data

            else:
                msg = "User is not correct"
                raise exceptions.AuthenticationFailed(msg)
        else:
            msg = "Must provide username"
            raise exceptions.AuthenticationFailed(msg)


class LoginSerializer(serializers.Serializer):
    username = serializers.CharField()
    password = serializers.CharField()

    def validate(self, data):
        username = data.get("username", "")
        password = data.get("password", "")

        if username and password:
            user = authenticate(username=username, password=password)
            if user:
                if user.is_active:
                    data["user"] = user
                else:
                    msg = "User is deactivated"
                    raise exceptions.AuthenticationFailed(msg)
            else:
                msg = "Unable to login with given credentials."
                raise exceptions.AuthenticationFailed(msg)
        else:
            msg = "Must provide username and password both."
            raise exceptions.AuthenticationFailed(msg)
        return data


class UserSerializer(serializers.ModelSerializer):
    email = serializers.EmailField(
        required=True,
        validators=[UniqueValidator(queryset=User.objects.all())]
    )
    username = serializers.CharField(
        validators=[UniqueValidator(queryset=User.objects.all())]
    )
    password = serializers.CharField(min_length=8)

    class Meta:
        model = User
        fields = ('username', 'email', 'password')


class PubgUserSerializer(serializers.ModelSerializer):

    email = serializers.EmailField(
        required=True,
        validators=[UniqueValidator(queryset=User.objects.all())]
    )
    username = serializers.CharField(
        validators=[UniqueValidator(queryset=User.objects.all())]
    )
    password = serializers.CharField(min_length=8)

    class Meta:
        model = PubgUser
        fields = ('firstName', 'lastName', 'phone', 'email', 'username', 'password')

    def create(self, validated_data):
        user = User.objects.create_user(validated_data['username'], validated_data['email'], validated_data['password'])
        if user:
            puser = PubgUser.objects.create(user=user, firstName=validated_data['firstName'],
                                        lastName=validated_data['lastName'], phone=validated_data['phone'])
            Wallet.objects.create(user=user)
            return "1"
        else:
            user.delete()
            return "0"


class EventSerializer(serializers.ModelSerializer):
    class Meta:
        model = Event
        fields = ('matchID', 'entryFree', 'winPrize', 'perKill', 'size', 'totalPeopleJoined',
                    'matchMap', 'title', 'matchType', 'matchVersion', 'timedata', 'img')


class TopPlayerSerializer(serializers.ModelSerializer):

    class Meta:
        model = PubgUser
        fields = ('user', 'amountWon')


class UpdateSerializer(serializers.ModelSerializer):

    class Meta:
        model = Update
        fields = ('version', 'updateinfo', 'updateurl', 'date')


class TransactionsSerializer(serializers.ModelSerializer):
    date = serializers.DateTimeField(format="%Y-%m-%d %H:%M:%S", required=False, read_only=True)

    class Meta:
        model = Transactions
        fields = ('amount', 'type', 'date', 'remark')


class ParticipantsListSerializer(serializers.ModelSerializer):
    class Meta:
        model = PlayerJoin
        fields = ('pubgusername', )


'''
    email = serializers.EmailField(
        required=True,
        validators=[UniqueValidator(queryset=User.objects.all())]
    )
    username = serializers.CharField(
        validators=[UniqueValidator(queryset=User.objects.all())]
    )
    password = serializers.CharField(min_length=8)

    def create(self, validated_data):
        user = User.objects.create_user(validated_data['username'], validated_data['email'],
                                        validated_data['password'])
        return user

'''
