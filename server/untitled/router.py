from Users.api.viewsets import PubgUserViewSet
from rest_framework import routers


router = routers.DefaultRouter()
router.register('Users', PubgUserViewSet, base_name='Users')
