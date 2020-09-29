from django.urls import path, include
from .views import EventList, TopPlayerList, UserRegister, UserLogin, UserLogout,\
    UserProfile, CheckSumApi, UpdateAPi, WalletApi, TransactionsList, RequestMoneyView, PlayerJoinView, ParticipantsList, \
    MatchDetails


urlpatterns = [
    path('AWM/', EventList.as_view(), name='events'),
    path('Tommy-Gun/', TopPlayerList.as_view(), name='topplayer'),
    path('FlareGun/', UserRegister.as_view()),
    path('M762/', UserLogin.as_view(), name='login'),
    path('KAR98k/', UserProfile.as_view(), name='profile'),
    path('logout/', UserLogout.as_view(), name='logout'),
    path('Mk47-Mutant/', CheckSumApi.as_view(), name='checksum'),
    path('FragGrenade/', UpdateAPi.as_view(), name='update'),
    path('Winchester/', WalletApi.as_view(), name='wallet'),
    path('SCAR-L/', TransactionsList.as_view(), name='translist'),
    path('Crossbow/', RequestMoneyView.as_view(), name='request'),
    path('Flares/', PlayerJoinView.as_view(), name='joinmatch'),
    path('Machine-Gun/', ParticipantsList.as_view(), name='playerlist'),
    path('Compensator/', MatchDetails.as_view(), name='matchdetails'),
]
