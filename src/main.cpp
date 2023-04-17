#include <QApplication>

#include "courseviewer.hpp"

int main(int argc, char *argv[]) {
    QApplication app(argc, argv);
	CourseViewer courseViewer;
	courseViewer.show();

	return app.exec();
}
