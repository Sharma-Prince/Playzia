# Generated by Django 2.1.5 on 2019-01-10 15:47

from django.db import migrations, models


class Migration(migrations.Migration):

    dependencies = [
        ('Users', '0001_initial'),
    ]

    operations = [
        migrations.RemoveField(
            model_name='transactions',
            name='status',
        ),
        migrations.AddField(
            model_name='transactions',
            name='date',
            field=models.DateField(auto_now_add=True, null=True),
        ),
        migrations.AddField(
            model_name='transactions',
            name='remark',
            field=models.CharField(max_length=30, null=True),
        ),
        migrations.AddField(
            model_name='transactions',
            name='type',
            field=models.CharField(max_length=10, null=True),
        ),
        migrations.AlterField(
            model_name='transactions',
            name='amount',
            field=models.CharField(max_length=10),
        ),
    ]
