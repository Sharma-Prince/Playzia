# Generated by Django 2.1.5 on 2019-01-12 15:16

from django.db import migrations, models


class Migration(migrations.Migration):

    dependencies = [
        ('Users', '0012_auto_20190112_2039'),
    ]

    operations = [
        migrations.AlterField(
            model_name='roompass',
            name='passID',
            field=models.CharField(blank=True, max_length=10, null=True),
        ),
        migrations.AlterField(
            model_name='roompass',
            name='roomID',
            field=models.CharField(blank=True, max_length=10, null=True),
        ),
    ]
