# Generated by Django 2.1.5 on 2019-01-12 06:13

from django.db import migrations, models
import django.db.models.deletion


class Migration(migrations.Migration):

    dependencies = [
        ('Users', '0006_auto_20190111_0239'),
    ]

    operations = [
        migrations.CreateModel(
            name='RoomPass',
            fields=[
                ('id', models.AutoField(auto_created=True, primary_key=True, serialize=False, verbose_name='ID')),
                ('roomID', models.CharField(default='', max_length=10)),
                ('passID', models.CharField(default='', max_length=10)),
                ('matchID', models.OneToOneField(on_delete=django.db.models.deletion.PROTECT, to='Users.Event')),
            ],
        ),
    ]