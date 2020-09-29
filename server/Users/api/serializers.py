from rest_framework import serializers
from Users.models import PubgUser
from django.contrib.auth.models import User
from rest_framework.validators import UniqueValidator


class UserSerializer(serializers.ModelSerializer):

    email = serializers.EmailField(
        required=True,
        validators=[UniqueValidator(queryset=PubgUser.objects.all())]
    )
    username = serializers.CharField(
        required=True,
        validators=[UniqueValidator(queryset=User.objects.all())]
    )
    password = serializers.CharField(max_length=8)

    def create(self, validated_data):
        user = User.objects.create(validated_data['username'], validated_data['password'])
        return user


class PubgUserSerializer(serializers.ModelSerializer):

    class Meta:
        model = PubgUser
        fields = ('email', 'firstName', 'lastName', 'phone')