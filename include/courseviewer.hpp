#ifndef __COURSEVIEWER_HPP__
#define __COURSEVIEWER_HPP__

#include "ui_courseviewer.h"

class CourseViewer: public QMainWindow {
	Q_OBJECT

public:
	explicit CourseViewer(QWidget* parent = nullptr);

private:
	Ui::CourseViewer ui;
};

#endif
