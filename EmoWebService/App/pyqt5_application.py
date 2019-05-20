import sys
import time
import os.path
import os

import PyQt5

dirname = os.path.dirname(PyQt5.__file__)
plugin_path = os.path.join(dirname, 'plugins', 'platforms')
os.environ['QT_QPA_PLATFORM_PLUGIN_PATH'] = plugin_path

from PyQt5.QtWidgets import QApplication, QWidget, QLabel, QPushButton, QCheckBox, QFileDialog, QVBoxLayout
from PyQt5.QtGui import QIcon
from PyQt5.QtCore import QThread, QObject, pyqtSignal, pyqtSlot

from textmining import *

LOCAL_PATH = os.path.dirname(os.path.dirname(TM_PATH))

print('LOCAL_PATH', LOCAL_PATH)

class TmEaTask(QObject):

    sig_done = pyqtSignal(int, int)  # worker id: emitted at end of work()
    sig_msg = pyqtSignal(str)  # message to be shown to user

    def __init__(self, id: int, config, pst_dir, text_dir, input_dir, emotion_dir, ax_dir, csv_dir, file_count, label_count):
        super().__init__()
        self.__id = id
        self.__abort = False
        self.notifyProgress = pyqtSignal(int)
        self.config = config
        self.pst_dir = pst_dir
        self.text_dir = text_dir
        self.input_dir = input_dir
        self.emotion_dir = emotion_dir
        self.ax_dir  = ax_dir
        self.csv_dir = csv_dir
        self.file_count = file_count
        self.label_count = label_count

    def update_config(self, config):
        self.config = config

    @pyqtSlot()
    def work(self):
        thread_name = QThread.currentThread().objectName()
        thread_id = int(QThread.currentThreadId())

        self.sig_msg.emit('start process...')

        # self.notifyProgress.emit(i)
        #print('workflow configuration:', self.config)

        total_count = [self.file_count, self.label_count]

        # self.notifyProgress.emit(i)

        print(self.pst_dir)
        print(self.text_dir)
        print(self.input_dir)
        print(self.emotion_dir)
        print(self.ax_dir)
        print(self.csv_dir)

        if self.config[0]:
            print('pst folder is', self.pst_dir)
            print('raw text folder is', self.text_dir)
            for i in parse_pst_in_folder(
                self.pst_dir,
                self.text_dir,
                {
                    "sender":"sender",
                    "sender_name":"sender_name",
                    "subject":"subject",
                    "time":"time",
                    "email":"email",
                    "receivers":"receivers",
                }
            ):
                self.sig_msg.emit(i)
            self.sig_msg.emit("finished parsing .PST files")

        if self.config[1]:
            print('raw text folder is', self.text_dir)
            print('packed text folder is', self.input_dir)
            for i in transform_text_file_to_pickle(
                self.text_dir,
                self.input_dir,
                'email_df_',
                ['sender', 'email', 'time', 'date'],
                total_count
            ):
                self.sig_msg.emit(i)
            self.sig_msg.emit("finished regulating raw text")
            self.file_count = total_count[0]

        if self.config[2]:
            print('packed text folder is', self.input_dir)
            print('labeled emotion folder is', self.emotion_dir)
            for i in app_extract_emotions(
                self.input_dir,
                'email_df_',
                self.emotion_dir,
                'emotion_',
                'email', 'time', 'date',
                total_count
            ):
                self.sig_msg.emit(i)
            self.sig_msg.emit("finished labeling emotions")
            self.label_count = total_count[1]

        if self.config[3]:
            print('labeled emotion folder is', self.emotion_dir)
            print('individual analytics folder is', self.ax_dir)
            print('CSV folder is', self.csv_dir)
            for i in app_analyze_emotions(
                self.emotion_dir,
                'emotion_',
                self.ax_dir,
                'analytics_',
                self.csv_dir,
                self.config[7]
            ):
                self.sig_msg.emit(i)
            self.sig_msg.emit("finished deep emotion analytics")

        if self.config[4]:
            print('labeled emotion folder is', self.emotion_dir)
            print('individual analytics folder is', self.ax_dir)
            for i in app_plot_emotion_series(
                self.emotion_dir,
                'emotion_',
                self.ax_dir,
            ):
                self.sig_msg.emit(i)
            self.sig_msg.emit("finished plotting emotion detection results")

        if self.config[5]:
            print('individual analytics folder is', self.ax_dir)
            for i in app_plot_analytics_results(
                self.ax_dir,
                'analytics_',
                self.ax_dir,
            ):
                self.sig_msg.emit(i)
            self.sig_msg.emit("finished plotting analytics results")

        if self.config[6]:
            print('CSV folder is', self.csv_dir)
            for i in app_plot_csv_results(
                self.csv_dir,
                'emotion_analysis_',
                self.csv_dir,
            ):
                self.sig_msg.emit(i)
            self.sig_msg.emit("finished plotting with CSV")

        self.sig_done.emit(self.file_count, self.label_count)

        print('finish')

    def abort(self):
        self.sig_msg.emit('Task abort')
        self.__abort = True


class AppEmotionAnalysis(QWidget):

    sig_abort_tmea_task = pyqtSignal()

    def __init__(self, px, py):
        super().__init__()

        self.title = 'Deep Emotion Analysis'
        self.left = px
        self.top = py
        self.width = 600
        self.height = 300

        self.configure_fn = os.path.join(LOCAL_PATH, 'config.json')
        self.configure = read_from_json_file(self.configure_fn)

        self.pst_dir = self.expand_path(self.configure['pst_dir'])
        self.text_dir = self.expand_path(self.configure['text_dir'])
        self.input_dir = self.expand_path(self.configure['input_dir'])
        self.emotion_dir = self.expand_path(self.configure['emotion_dir'])
        self.individual_ax_dir = self.expand_path(self.configure['ax_dir'])
        self.org_ax_dir = self.expand_path(self.configure['csv_dir'])

        self.initUI()
        QThread.currentThread().setObjectName('main')

        self.tmEaTask = None
        self.thread = None


    def expand_path(self, path):
        #print('expand_path LOCAL_PATH', LOCAL_PATH)
        if len(path) > 2:
            if path.startswith('/'):
                return path
            elif path[1] == ':':
                return path
            elif path.startswith('\\'):
                path = path.replace('\\', '')
                return os.path.join(LOCAL_PATH, path)
            else:
                return os.path.join(LOCAL_PATH, path)
        return path


    def simplify_path(self, path):
        #print('simplify_path LOCAL_PATH', LOCAL_PATH)
        if path.startswith(LOCAL_PATH):
            text_dir = path.replace(LOCAL_PATH, '')
            if text_dir.startswith('/'):
                text_dir = text_dir[1:]
            elif text_dir.startswith('\\'):
                text_dir = text_dir.replace('\\', '')
            return text_dir
        return path


    def save_configure(self):
        d = {
            "pst_dir" : self.simplify_path(self.pst_dir),
            "text_dir" : self.simplify_path(self.text_dir),
            "input_dir" : self.simplify_path(self.input_dir),
            "emotion_dir" : self.simplify_path(self.emotion_dir),
            "ax_dir" : self.simplify_path(self.individual_ax_dir),
            "csv_dir" : self.simplify_path(self.org_ax_dir),
            "file_count" : self.configure['file_count'],
            "label_count" : self.configure['label_count'],
            "check_box" : self.configure['check_box']
        }
        save_to_json_file(d, self.configure_fn)


    def initUI(self):
        layout = QVBoxLayout()

        self.setWindowTitle(self.title)
        self.setGeometry(self.left, self.top, self.width, self.height)

        self.btn_slct_dir_pst = QPushButton('Select *.PST files Directory', self)
        self.btn_slct_dir_pst.setToolTip('Select *.PST files directory, default is set')
        self.btn_slct_dir_pst.clicked.connect(self.slct_dir_pst)

        self.btn_slct_dir_raw_text = QPushButton('Select Raw Text Directory', self)
        self.btn_slct_dir_raw_text.setToolTip('Select raw text directory, default is set')
        self.btn_slct_dir_raw_text.clicked.connect(self.slct_dir_raw_text)

        self.btn_slct_dir_regulated_text = QPushButton('Select Regulated Text-List Directory', self)
        self.btn_slct_dir_regulated_text.setToolTip('Select regulated text-list directory, default is set')
        self.btn_slct_dir_regulated_text.clicked.connect(self.slct_dir_regulated_text)

        self.btn_slct_dir_labeled_emotions = QPushButton('Select Labeled Emotions Directory', self)
        self.btn_slct_dir_labeled_emotions.setToolTip('Select labeled emotions directory, default is set')
        self.btn_slct_dir_labeled_emotions.clicked.connect(self.slct_dir_labeled_emotions)

        self.btn_slct_dir_individual_analytics = QPushButton('Select Individual Analytics Directory', self)
        self.btn_slct_dir_individual_analytics.setToolTip('Select individual analytics directory, default is set')
        self.btn_slct_dir_individual_analytics.clicked.connect(self.slct_dir_individual_analytics)

        self.btn_slct_dir_org_analytics = QPushButton('Select Organization Analysis Directory', self)
        self.btn_slct_dir_org_analytics.setToolTip('Select organization analysis directory, default is set')
        self.btn_slct_dir_org_analytics.clicked.connect(self.slct_dir_org_analytics)

        layout.addWidget(self.btn_slct_dir_pst)
        layout.addWidget(self.btn_slct_dir_raw_text)
        layout.addWidget(self.btn_slct_dir_regulated_text)
        layout.addWidget(self.btn_slct_dir_labeled_emotions)
        layout.addWidget(self.btn_slct_dir_individual_analytics)
        layout.addWidget(self.btn_slct_dir_org_analytics)

        self.chkbx_pst = QCheckBox("Parse .pst files")
        self.chkbx_rt = QCheckBox("Regulate Text files")
        self.chkbx_de = QCheckBox("Detecting Emotions")
        self.chkbx_cr = QCheckBox("Compute Colley's rating")
        self.chkbx_ax = QCheckBox("Run Deep Analytics")
        self.chkbx_ge = QCheckBox("Plot Detected Emotions")
        self.chkbx_ga = QCheckBox("Plot Deep Analytics")
        self.chkbx_gr = QCheckBox("Plot From CSV")


        self.chkbx_pst.setChecked(True)
        self.chkbx_rt.setChecked(True)
        self.chkbx_de.setChecked(True)
        self.chkbx_cr.setChecked(True)
        self.chkbx_ax.setChecked(True)
        self.chkbx_ge.setChecked(True)
        self.chkbx_ga.setChecked(True)
        self.chkbx_gr.setChecked(True)

        layout.addWidget(self.chkbx_pst)
        layout.addWidget(self.chkbx_rt)
        layout.addWidget(self.chkbx_de)
        layout.addWidget(self.chkbx_cr)
        layout.addWidget(self.chkbx_ax)
        layout.addWidget(self.chkbx_ge)
        layout.addWidget(self.chkbx_ga)
        layout.addWidget(self.chkbx_gr)

        self.btn_run = QPushButton('RUN', self)
        self.btn_run.setToolTip('Run the process')
        self.btn_run.clicked.connect(self.run_analytics)
        layout.addWidget(self.btn_run)

        self.l_status = QLabel()
        self.l_status.setText("Click RUN to start!")
        layout.addWidget(self.l_status)

        self.setLayout(layout)
        self.show()


    @pyqtSlot()
    def slct_dir_pst(self):
        self.pst_dir = str(QFileDialog.getExistingDirectory(self,
            "Select Directory",
            LOCAL_PATH,
            )
        )
        if PLATFORM == WIN:
            self.pst_dir = self.pst_dir.replace('/', '\\')
        elif PLATFORM == MAC:
            self.pst_dir = self.pst_dir.replace('\\', '/')
        if self.pst_dir is not None:
            print ('*.PST directory is: {0}'.format(self.pst_dir))


    @pyqtSlot()
    def slct_dir_raw_text(self):
        self.text_dir = str(QFileDialog.getExistingDirectory(self,
            "Select Directory",
            LOCAL_PATH,
            )
        )
        if PLATFORM == WIN:
            self.text_dir = self.text_dir.replace('/', '\\')
        elif PLATFORM == MAC:
            self.text_dir = self.text_dir.replace('\\', '/')
        if self.text_dir is not None:
            print ('input text directory is: {0}'.format(self.text_dir))


    @pyqtSlot()
    def slct_dir_regulated_text(self):
        self.input_dir = str(QFileDialog.getExistingDirectory(self,
            "Select Directory",
            LOCAL_PATH,
            )
        )
        if PLATFORM == WIN:
            self.input_dir = self.input_dir.replace('/', '\\')
        elif PLATFORM == MAC:
            self.input_dir = self.input_dir.replace('\\', '/')
        if self.input_dir is not None:
            print ('regulated file directory is: {0}'.format(self.input_dir))


    @pyqtSlot()
    def slct_dir_labeled_emotions(self):
        self.emotion_dir = str(QFileDialog.getExistingDirectory(self,
            "Select Directory",
            LOCAL_PATH,
            )
        )
        if PLATFORM == WIN:
            self.emotion_dir = self.emotion_dir.replace('/', '\\')
        elif PLATFORM == MAC:
            self.emotion_dir = self.emotion_dir.replace('\\', '/')
        if self.emotion_dir is not None:
            print ('labeled emotion directory is: {0}'.format(self.emotion_dir))


    @pyqtSlot()
    def slct_dir_individual_analytics(self):
        self.individual_ax_dir = str(QFileDialog.getExistingDirectory(self,
            "Select Directory",
            LOCAL_PATH,
            )
        )
        if PLATFORM == WIN:
            self.individual_ax_dir = self.individual_ax_dir.replace('/', '\\')
        elif PLATFORM == MAC:
            self.individual_ax_dir = self.individual_ax_dir.replace('\\', '/')
        if self.individual_ax_dir is not None:
            print ('individual analytics directory is: {0}'.format(self.individual_ax_dir))


    @pyqtSlot()
    def slct_dir_org_analytics(self):
        self.org_ax_dir = str(QFileDialog.getExistingDirectory(self,
            "Select Directory",
            LOCAL_PATH,
            )
        )
        if PLATFORM == WIN:
            self.org_ax_dir = self.org_ax_dir.replace('/', '\\')
        elif PLATFORM == MAC:
            self.org_ax_dir = self.org_ax_dir.replace('\\', '/')
        if self.org_ax_dir is not None:
            print ('organization output directory is: {0}'.format(self.org_ax_dir))


    @pyqtSlot(str)
    def update_progress(self, s):
        self.l_status.setText(s)


    @pyqtSlot(int, int)
    def task_completed(self, file_count, label_count):
        self.btn_run.setEnabled(True)
        self.l_status.setText("Task Finished")
        self.configure['file_count'] = file_count
        self.configure['label_count'] = label_count
        self.save_configure()


    def check_input_folder(self):
        if not os.path.exists(self.pst_dir):
            if not os.path.isdir(self.pst_dir):
                return False, ".pst folder is not available"

        if not os.path.exists(self.text_dir):
            if not os.path.isdir(self.text_dir):
                return False, "raw text folder is not available"

        if not os.path.exists(self.input_dir):
            if not os.path.isdir(self.input_dir):
                return False, "regulated text folder is not available"

        if not os.path.exists(self.emotion_dir):
            if not os.path.isdir(self.emotion_dir):
                return False, "labeled emotion folder is not available"

        if not os.path.exists(self.individual_ax_dir):
            if not os.path.isdir(self.individual_ax_dir):
                return False, "emotion analytics folder is not available"

        if not os.path.exists(self.org_ax_dir):
            if not os.path.isdir(self.org_ax_dir):
                return False, "CSV folder is not available"

        return True, "Start analysis..."


    @pyqtSlot()
    def run_analytics(self):
        ready, info = self.check_input_folder()
        if not ready:
            self.l_status.setText(info)
            return

        self.configure['check_box'] = [
            self.chkbx_pst.isChecked(),
            self.chkbx_rt.isChecked(),
            self.chkbx_de.isChecked(),
            self.chkbx_ax.isChecked(),
            self.chkbx_ge.isChecked(),
            self.chkbx_ga.isChecked(),
            self.chkbx_gr.isChecked(),
            self.chkbx_cr.isChecked(),
        ]
        self.l_status.setText(info)


        self.tmEaTask = TmEaTask(
            0,
            config=self.configure['check_box'],
            pst_dir=self.pst_dir,
            text_dir=self.text_dir,
            input_dir=self.input_dir,
            emotion_dir=self.emotion_dir,
            ax_dir=self.individual_ax_dir,
            csv_dir=self.org_ax_dir,
            file_count=self.configure['file_count'],
            label_count=self.configure['label_count'],
        )


        self.thread = QThread(self)
        #print(self.thread)

        self.thread.setObjectName('thread_tmea')
        self.tmEaTask.moveToThread(self.thread)
        self.tmEaTask.sig_msg.connect(self.update_progress)
        self.tmEaTask.sig_done.connect(self.task_completed)
        self.sig_abort_tmea_task.connect(self.tmEaTask.abort)

        self.thread.started.connect(self.tmEaTask.work)
        # this will emit 'started' and start thread's event loop

        #print('start thread')

        self.btn_run.setEnabled(False)
        self.thread.start()



if __name__ == '__main__':
    app = QApplication(sys.argv)
    screen_resolution = app.desktop().screenGeometry()
    width, height = screen_resolution.width(), screen_resolution.height()
    ex = AppEmotionAnalysis(width/2-300, height/2-150)
    sys.exit(app.exec_())

