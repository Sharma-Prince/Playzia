# Generated by Django 2.1.5 on 2019-01-12 18:47

from django.db import migrations, models


class Migration(migrations.Migration):

    dependencies = [
        ('Users', '0015_auto_20190113_0014'),
    ]

    operations = [
        migrations.AlterField(
            model_name='event',
            name='passID',
            field=models.CharField(blank=True, max_length=10, null=True),
        ),
        migrations.AlterField(
            model_name='event',
            name='roomID',
            field=models.CharField(blank=True, max_length=10, null=True),
        ),
    ]
