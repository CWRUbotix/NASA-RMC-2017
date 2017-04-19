#ifndef CONSUMERTHREAD_H
#define CONSUMERTHREAD_H

#include <QThread>
#include <AMQPcpp.h>
#include "messages.pb.h"

using namespace com::cwrubotix::glennifer;

class ConsumerThread : public QThread
{
    Q_OBJECT
public:
    ConsumerThread(AMQP *amqp);
protected:
    void run();
signals:
    void stateReady(State *s);
private:
    AMQP *m_amqp;

};

extern ConsumerThread *instance;

int handleReceivedState(AMQPMessage *message);

#endif // CONSUMERTHREAD_H
