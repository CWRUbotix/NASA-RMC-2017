#include "consumerthread.h"
#include <AMQPcpp.h>
#include <QDebug>
#include "messages.pb.h"

using namespace com::cwrubotix::glennifer;

ConsumerThread::ConsumerThread(AMQP *amqp) {
    m_amqp = new AMQP("192.168.0.200");
    instance = this;
}

void ConsumerThread::run() {
    AMQPQueue *queue = m_amqp->createQueue("abcde");
    queue->Declare();
    queue->Bind("amq.topic", queue->getName());
    queue->addEvent(AMQP_MESSAGE, handleReceivedState);
    queue->Consume(AMQP_NOACK);
}

int handleReceivedState(AMQPMessage *message) {
    uint32_t len = 0;
    char *data = message->getMessage(&len);
    if (!data) {
        qDebug() << "No data";
        return 0;
    }
    State *s = new State();
    s->ParseFromArray(data, len);
    emit instance->stateReady(s);
    return 0;
}

void doneWithState(State *s) {
    delete s;
}

ConsumerThread *instance;
