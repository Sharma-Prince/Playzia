from django.contrib import admin
from .models import PubgUser, Wallet, Event, RequestMoney, Transactions, PlayerJoin, SentMoney, Update
from django.urls import path
from django.http import HttpResponse


class PaytmHistoryAdmin(admin.ModelAdmin):
    list_display = ('user', 'orderid', 'txndeta', 'txnamount', 'status')


class PubgUserAdmin(admin.ModelAdmin):
    list_display = ['user', 'phone', 'firstName', 'lastName', 'myKills', 'matchesPlayed', 'amountWon']

from .models import PaytmHistory


class PageEventAdmin(admin.ModelAdmin):
    list_display = ['matchID', 'entryFree', 'winPrize', 'perKill', 'size', 'totalPeopleJoined',
                    'matchMap', 'title', 'matchType', 'matchVersion', 'timedata', 'totalPeopleJoined', 'active']
    list_filter = ['active']

    def get_queryset(self, request):
        join = super(PageEventAdmin, self).get_queryset(request)
        return join.filter(active=True)


class WalletAdmin(admin.ModelAdmin):
    list_display = [field.name for field in Wallet._meta.get_fields()]


class RequestMoneyAdmin(admin.ModelAdmin):
    list_display = [field.name for field in RequestMoney._meta.get_fields()]

    def save_model(self, request, obj, form, change):
        super(RequestMoneyAdmin, self).save_model(request, obj, form, change)
        trans = Transactions()
        trans.user = obj.user
        trans.amount = obj.money
        trans.type = 'debit'
        trans.remark = 'Withdraw'
        trans.save()

    def get_queryset(self, request):
        join = super(RequestMoneyAdmin, self).get_queryset(request)
        return join.filter(paid='no')


class TransactionsAdmin(admin.ModelAdmin):
    list_display = [field.name for field in Transactions._meta.get_fields()]


class SentMoneyAdmin(admin.ModelAdmin):
    list_display = [field.name for field in SentMoney._meta.get_fields()]
    list_filter = ['user']


class PlayerJoinAdmin(admin.ModelAdmin):
    list_display = [field.name for field in PlayerJoin._meta.get_fields()]
    list_filter = ['matchID_id', 'update']
    change_list_template = 'admin/users/users_change_list.html'

    def save_model(self, request, obj, form, change):
        super(PlayerJoinAdmin, self).save_model(request, obj, form, change)
        puser = PubgUser.objects.get(user=obj.user)
        puser.myKills += obj.matchKill
        puser.matchesPlayed += 1
        puser.amountWon += obj.moneyEarn
        puser.save()
        wallet = Wallet.objects.get(user=obj.user)
        wallet.balance += obj.moneyEarn
        wallet.save()

    def get_queryset(self, request):
        join = super(PlayerJoinAdmin, self).get_queryset(request)
        if join.update:
            return join.filter(update=False)
        return join



'''
    def save_model(self, request, obj, form, change):
        puser = PubgUser.objects.get(user=obj.user)
        puser.myKills += obj.matchKill
        puser.matchesPlayed += 1
        puser.amountWon += obj.moneyEarn
        puser.save()
        '''


admin.site.register(PubgUser, PubgUserAdmin)
admin.site.register(Event, PageEventAdmin)
admin.site.register(Wallet, WalletAdmin)
admin.site.register(RequestMoney, RequestMoneyAdmin)
admin.site.register(Transactions, TransactionsAdmin)
admin.site.register(PlayerJoin, PlayerJoinAdmin)
admin.site.register(SentMoney, SentMoneyAdmin)
admin.site.register(PaytmHistory)
admin.site.register(Update)
