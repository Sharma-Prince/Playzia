# Generated by Django 2.1.5 on 2019-01-12 12:50

from django.db import migrations, models


class Migration(migrations.Migration):

    dependencies = [
        ('Users', '0008_auto_20190112_1147'),
    ]

    operations = [
        migrations.AddField(
            model_name='event',
            name='passID',
            field=models.CharField(default='', max_length=10, null=True),
        ),
        migrations.AddField(
            model_name='event',
            name='roomID',
            field=models.CharField(default='', max_length=10, null=True),
        ),
    ]
