# Generated by Django 2.1.5 on 2019-01-12 15:17

from django.db import migrations, models


class Migration(migrations.Migration):

    dependencies = [
        ('Users', '0013_auto_20190112_2046'),
    ]

    operations = [
        migrations.AlterField(
            model_name='roompass',
            name='passID',
            field=models.CharField(default='', max_length=10, null=True),
        ),
        migrations.AlterField(
            model_name='roompass',
            name='roomID',
            field=models.CharField(default='', max_length=10, null=True),
        ),
    ]
