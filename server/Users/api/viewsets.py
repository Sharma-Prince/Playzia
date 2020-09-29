from Users.models import PubgUser
from django.contrib.auth.models import User
from .serializers import PubgUserSerializer, UserSerializer
from rest_framework import viewsets
from rest_framework.response import Response


class PubgUserViewSet(viewsets.ViewSet):

    def list(self, request):
        queryset = PubgUser.objects.all()
        serializer = PubgUserSerializer(queryset, many=True)
        return Response(serializer.data)


class UserViewSet(viewsets.ViewSet):

    def create(self,request):
        pass




'''


class PubgUserViewSet(viewsets.ModelViewSet):
    queryset = PubgUser.objects.all()
    serializer_class = PubgUserSerializer



'''