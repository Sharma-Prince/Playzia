# Generated by Django 2.1.5 on 2019-01-14 14:52

from django.db import migrations, models


class Migration(migrations.Migration):

    dependencies = [
        ('Users', '0020_auto_20190114_2019'),
    ]

    operations = [
        migrations.AlterField(
            model_name='requestmoney',
            name='paid',
            field=models.CharField(default='no', max_length=10),
        ),
    ]
