#include "cameraone.h"
#include "ui_cameraone.h"
#include "consumerthread.h"
//#include "messages.pb.h"
typedef unsigned char byte;

//using namespace std;
CameraOne::CameraOne(QWidget *parent, QString login) :
    QDialog(parent),
    ui(new Ui::CameraOne)
{
    ui->setupUi(this);
    str_login = login;
}

CameraOne::~CameraOne()
{
    delete ui;
}

void CameraOne::on_label_linkActivated() {
    cv::cvtColor(CameraOne::frame, CameraOne::frame, CV_BGR2RGB);
    ui->cam1lbl->setPixmap(QPixmap::fromImage(QImage(CameraOne::frame.data, CameraOne::frame.cols, CameraOne::frame.rows, CameraOne::frame.step, QImage::Format_RGB888)));
    //qDebug("Got here 2");
}

void CameraOne::handleFrame(QString key, QByteArray data) {
    /*std::vector<byte> vectordata(data.begin(), data.end());
    cv::Mat data_mat(vectordata, true);
    cv::Mat image(cv::imdecode(data_mat, 1));
    frame = image;*/
    //frame = cv::imdecode(data, CV_LOAD_IMAGE_COLOR);
    std::vector<byte> bufferToCompress(data.begin(), data.end());
    cv::Mat image = cv::Mat(480,640,CV_8UC3,bufferToCompress.data()).clone(); // copy
    //480,640
    frame = image;
    CameraOne::on_label_linkActivated();
    //qDebug("Got here");
}

void CameraOne::camOneSubscription() {
    QString login = str_login;
    ConsumerThread *thread = new ConsumerThread(str_login, "camera.one");
    qDebug("Got here 4");
    connect(thread, &ConsumerThread::receivedMessage, this, &CameraOne::handleFrame);
    connect(thread, SIGNAL(finished()), thread, SLOT(deleteLater()));
    thread->start();
    qDebug("Got here 7");
   // AMQPExchange * ex = m_amqp->createExchange("amq.topic");
    //ex->Declare("amq.topic", "topic", AMQP_DURABLE);
    //qDebug("Got here 6");
    //ex->Publish((char*)msg_buff, msg_size, "state.subscribe");
}

void CameraOne::camOneStream() {
    qDebug("Got here 3");
    CameraOne::camOneSubscription();
}
