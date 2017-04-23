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

void CameraOne::on_label_linkActivated(cv::Mat mat) { //took argument off cv::Mat frame
    cv::Mat frame;
    qDebug("Got here start link");
    cv::cvtColor(mat, frame, CV_LOAD_IMAGE_COLOR); //changed to temp and took off CV_BGR2RGB
    qDebug("Got here cvtColor");
    ui->cam1lbl->setPixmap(QPixmap::fromImage(QImage(frame.data, frame.cols, frame.rows, frame.step, QImage::Format_RGB888)));
    qDebug("Got here 2");
}

void CameraOne::handleFrame(QString key, QByteArray data) {
    QImage image((uchar*)data.data(), 640, 480, QImage::Format_RGB888);
    qDebug("Image loaded");
    ui->cam1lbl->setPixmap(QPixmap::fromImage(image));

    /*qDebug("Start handleFrame");
    cv::Mat inmat(std::vector<uchar>(data.begin(), data.end()));
    cv::Size s = inmat.size();
    std::string r = std::to_string(s.height);
    std::string c = std::to_string(s.width);*/

    //const char * r2 = r.c_str();
    //const char * c2 = c.c_str();

    //std::cout << r;
    //std::cout << c;
    /*qDebug(QString::fromStdString(r).toLatin1());
    qDebug(QString::fromStdString(c).toLatin1());
    qDebug("Inmat assgined");

    QPixmap pixmap = QPixmap::fromImage(
        QImage(
            (unsigned char *) data.data(),
            640,
            480,
            QImage::Format_RGB888
        )
    );
    qDebug("pixmap assigned");

    QImage img= QImage((uchar*) inmat.data, inmat.cols, inmat.rows, inmat.step, QImage::Format_RGB888);
    qDebug("Qimage assigned");*/

    //ui->cam1lbl->setPixmap(QPixmap::fromImage(img));
    //ui->cam1lbl->setPixmap(pixmap); qDebug("pixmap set to label");
    qDebug("ui-setPixmap");
    //CameraOne::on_label_linkActivated(inmat);

    //DOESNT CRASH UP TO HERE YAY!

    //cv::namedWindow("Camera", CV_WINDOW_AUTOSIZE);
    //cv::imshow("Camera", inmat);
    /*std::vector<byte> vectordata(data.begin(), data.end());
    cv::Mat data_mat(vectordata, true);
    cv::Mat image(cv::imdecode(data_mat, 1));
    frame = image;*/
    //cv::Mat frame;
    //frame = cv::imdecode(data, CV_LOAD_IMAGE_COLOR);

    //std::vector<byte> bufferToCompress(data.begin(), data.end());

    //byte begin = reinterpret_cast<byte>(data.data());
    //byte end = begin + data.length();
    //std::vector<byte> bufferToCompress(begin, end);

    //QImage img((const uchar*)data.data(), 640, 480, QImage::Format_RGB16);

    //qDebug("Got here10");
    //cv::Mat temp = cv::Mat(480, 640, CV_8UC3, bufferToCompress.data()).clone(); // clone

    //cv::Mat temp(640, 480, CV_8UC3, (uchar*)img.bits(), img.bytesPerLine());
    //qDebug("Got here9");
    //temp.copyTo(frame);
    //qDebug("Got here8");
    /*most recent byte * pByte = reinterpret_cast<byte*>(data.data());
    QImage img(pByte,480,640,QImage::Format_RGB888); //original Format_RGB888 and (const uchar*)data.data()
    qDebug("Got here qimg");
    img.save("qimagetest.png");
    ui->cam1lbl->setPixmap(QPixmap::fromImage(img));*/
    //cv::Mat tmp(img.height(),img.width(), CV_8UC3,(uchar*)img.bits(),img.bytesPerLine());
    //qDebug("Got here made tmp/set Pixmap");
    //cv::Mat res;
    //qDebug("Got here made res");
    //cv::cvtColor(tmp, res, CV_BGR2RGB);
    //qDebug("Got here tmp to res");
    //*image_uchar = data.data();
    //cv::Mat temp = cv::Mat(480, 640, CV_8UC3, image_uchar);
    //CameraOne::on_label_linkActivated(res);
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
