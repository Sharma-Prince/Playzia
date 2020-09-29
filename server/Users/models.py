from django.db import models
from django.contrib.auth.models import User
from django.contrib.auth.models import AbstractUser
from django.conf import settings
from django.utils import timezone


def increment():
    count = PaytmHistory.objects.all().order_by('id').last()
    if not count:
        return str('20300000')
    id = count.id
    return str(id + 20300000)


class PaytmHistory(models.Model):
    user = models.ForeignKey(settings.AUTH_USER_MODEL, on_delete=models.PROTECT)
    orderid = models.CharField(default=increment, editable=False, max_length=30, unique=True)
    txndeta = models.DateTimeField(auto_now_add=True, null=True)
    transactionid = models.CharField(max_length=120, null=True, editable=False)
    txnamount = models.FloatField(default=0.0)
    status = models.CharField(max_length=12, default='pending', editable=False)

    def __str__(self):
        return self.orderid


class PubgUser(models.Model):
    userID = models.AutoField(primary_key=True)
    user = models.OneToOneField(User, on_delete=models.PROTECT, to_field='username')
    firstName = models.CharField(max_length=25, null=True, blank=True)
    lastName = models.CharField(max_length=25, null=True, blank=True)
    phone = models.CharField(max_length=10, null=True, blank=True)
    myKills = models.IntegerField(default=0)
    matchesPlayed = models.IntegerField(default=0)
    amountWon = models.IntegerField(default=0)
    created_at = models.DateTimeField(auto_now_add=True)
    updated_at = models.DateTimeField(auto_now=True)

    def __str__(self):
        return self.user.username


class Event(models.Model):
    matchID = models.AutoField(primary_key=True)
    entryFree = models.IntegerField()
    winPrize = models.IntegerField()
    perKill = models.IntegerField()
    size = models.IntegerField()
    totalPeopleJoined = models.IntegerField()
    matchMap = models.CharField(max_length=25)
    title = models.CharField(max_length=25)
    matchType = models.CharField(max_length=25)
    matchVersion = models.CharField(max_length=25)
    timedata = models.DateTimeField()
    totalPeopleJoined = models.IntegerField(default=0)
    img = models.URLField(blank=True,default="")
    roomID = models.CharField(max_length=10, blank=True, default="")
    passID = models.CharField(max_length=10, blank=True, default="")
    active = models.BooleanField(default=True)
    created_at = models.DateTimeField(auto_now_add=True)
    updated_at = models.DateTimeField(auto_now=True)

    def __str__(self):
        return str(self.matchID)


class Wallet(models.Model):
    user = models.OneToOneField(User, on_delete=models.PROTECT)
    walletID = models.AutoField(primary_key=True)
    balance = models.FloatField(default=0)
    active = models.BooleanField(default=True)
    created_at = models.DateTimeField(auto_now_add=True)
    updated_at = models.DateTimeField(auto_now=True)

    def __str__(self):
        return str(self.walletID)


class RequestMoney(models.Model):
    user = models.ForeignKey(User, on_delete=models.PROTECT)
    RequestID = models.AutoField(primary_key=True)
    money = models.FloatField(default=0)
    phone = models.CharField(max_length=20)
    paid = models.CharField(default='no', max_length=10) #yes
    created_at = models.DateTimeField(auto_now_add=True)
    updated_at = models.DateTimeField(auto_now=True)

    def __str__(self):
        return self.user.username


class SentMoney(models.Model):
    user = models.ForeignKey(User, on_delete=models.PROTECT)
    amount = models.IntegerField()
    status = models.CharField(default='failed', max_length=10)
    created_at = models.DateTimeField(auto_now_add=True)
    updated_at = models.DateTimeField(auto_now=True)


class Transactions(models.Model):
    TransID = models.AutoField(primary_key=True)
    user = models.ForeignKey(User, on_delete=models.PROTECT)
    amount = models.CharField(max_length=10)
    type = models.CharField(max_length=10, null=True)
    date = models.DateTimeField(auto_now_add=True, null=True)
    remark = models.CharField(max_length=30, null=True)
    created_at = models.DateTimeField(auto_now_add=True)
    updated_at = models.DateTimeField(auto_now=True)

    def __str__(self):
        return str(self.TransID)


class PlayerJoin(models.Model):
    matchID = models.ForeignKey(Event, on_delete=models.PROTECT)
    user = models.ForeignKey(User, on_delete=models.PROTECT)
    pubgusername = models.CharField(max_length=25, null=True)
    matchKill = models.IntegerField(default=0)
    moneyEarn = models.IntegerField(default=0)
    update = models.BooleanField(default=False)
    type = models.CharField(max_length=10, null=True)
    created_at = models.DateTimeField(auto_now_add=True)
    updated_at = models.DateTimeField(auto_now=True)

    def __str__(self):
        return self.pubgusername


class Update(models.Model):
    version = models.CharField(max_length=20)
    updateinfo = models.CharField(max_length=225)
    updateurl = models.CharField(max_length=50)
    date = models.DateField(null=True)

    def __str__(self):
        return self.version










'''
from django.db import models
from django.contrib.auth.models import User
from django.contrib.auth.models import AbstractUser


class PubgUser(models.Model):
    userID = models.AutoField(primary_key=True)
    user = models.OneToOneField(User, on_delete=models.PROTECT)
    email = models.EmailField(max_length=70, null=True, blank=True)  # unique=True
    firstName = models.CharField(max_length=25, null=True, blank=True)
    lastName = models.CharField(max_length=25, null=True, blank=True)
    phone = models.CharField(max_length=10, null=True, blank=True)
    myKills = models.IntegerField(default=0)
    matchesPlayed = models.IntegerField(default=0)
    amountWon = models.IntegerField(default=0)
    created_at = models.DateTimeField(auto_now_add=True)
    updated_at = models.DateTimeField(auto_now=True)

    def __str__(self):
        return self.user.username


class Event(models.Model):
    matchID = models.AutoField(primary_key=True)
    entryFree = models.IntegerField()
    winPrize = models.IntegerField()
    perKill = models.IntegerField()
    size = models.IntegerField()
    totalPeopleJoined = models.IntegerField()
    matchMap = models.CharField(max_length=25)
    title = models.CharField(max_length=25)
    matchType = models.CharField(max_length=25)
    matchVersion = models.CharField(max_length=25)
    timedata = models.DateTimeField()
    totalPeopleJoined = models.IntegerField(default=0)
    active = models.BooleanField(default=True)
    created_at = models.DateTimeField(auto_now_add=True)
    updated_at = models.DateTimeField(auto_now=True)

    def __str__(self):
        return str(self.matchID)


class Wallet(models.Model):
    user = models.OneToOneField(User, on_delete=models.PROTECT)
    walletID = models.AutoField(primary_key=True)
    balance = models.IntegerField(default=0)
    active = models.BooleanField(default=True)
    created_at = models.DateTimeField(auto_now_add=True)
    updated_at = models.DateTimeField(auto_now=True)

    def __str__(self):
        return str(self.walletID)


class RequestMoney(models.Model):
    user = models.OneToOneField(User, on_delete=models.PROTECT)
    RequestID = models.AutoField(primary_key=True)
    money = models.IntegerField(default=0)
    phone = models.CharField(max_length=10)
    paid = models.CharField(default='no', max_length=10) #yes
    created_at = models.DateTimeField(auto_now_add=True)
    updated_at = models.DateTimeField(auto_now=True)


class SentMoney(models.Model):
    user = models.ForeignKey(User, on_delete=models.PROTECT)
    amount = models.IntegerField()
    status = models.CharField(default='failed', max_length=10)
    created_at = models.DateTimeField(auto_now_add=True)
    updated_at = models.DateTimeField(auto_now=True)


class Transactions(models.Model):
    TransID = models.AutoField(primary_key=True)
    user = models.ForeignKey(User, on_delete=models.PROTECT)
    amount = models.IntegerField()
    status = models.CharField(default="failed", max_length=10)
    created_at = models.DateTimeField(auto_now_add=True)
    updated_at = models.DateTimeField(auto_now=True)

    def __str__(self):
        return str(self.TransID)


class PlayerJoin(models.Model):
    joinID = models.OneToOneField(Transactions, on_delete=models.PROTECT)
    matchID = models.ForeignKey(Event, on_delete=models.PROTECT)
    user = models.ForeignKey(User, on_delete=models.PROTECT)
    pubgusername = models.CharField(max_length=25,null=True)
    matchKill = models.IntegerField(default=0)
    moneyEarn = models.IntegerField(default=0)
    update = models.BooleanField(default=False)
    created_at = models.DateTimeField(auto_now_add=True)
    updated_at = models.DateTimeField(auto_now=True)

    def __str__(self):
        return self.pubgusername
'''
