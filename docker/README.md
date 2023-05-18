# Local Installation of ActiveMQ in Docker  
docker-compose --log-level DEBUG --file plantit-common/docker/activmq.yml up --no-start

## ActiveMQ-Specfications

## Ports:
   *  MQTT: 1883 (evtl für IOT-Kurs benötigt)
   *  JMS: 16616
   *  Admin Console: 8161 (http://localhost:8161/admin)

## Zugänge:

* Admin-Konsole: user: admin passwd: cds4admin
* JMS: user: cds passwd: cds4All (Brockerzugang)

weitere siehe activemq.yml


