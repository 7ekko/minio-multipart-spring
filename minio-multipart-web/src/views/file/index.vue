<template>
  <div id="app">
    <div style="margin-left: 10%; margin-right: 10%;">
      <el-card style='width: 100%;'>
        <el-upload ref="upload" class="upload-demo" action="" :on-success="handleSuccess" :http-request="handleUpload"
          :on-change="handleChange"
           :show-file-list="false">
          <el-button slot="trigger" size="small">选取文件</el-button>
        </el-upload>
        <span style='width: 100%;' v-if="showUploadProgress">正在上传文件: {{ uploadingFileName }}</span>
        <el-progress class="upload-progress" v-if="showUploadProgress" :text-inside="true" :stroke-width="18"
          :percentage="uploadProgress" status="success" />
      </el-card>
    </div>
    <div style="margin-left: 10%; margin-right: 10%;">
      <el-card>
        <el-table :data="fileList" style="width: 100%">
          <el-table-column prop="fileName" label="文件名" width="750" min-width="80">
            <template slot-scope="scope">
              <div class="file-name-container" @mouseover="showButtons(scope.$index)"
                @mouseleave="hideButtons(scope.$index)">
                {{ scope.row.fileName }}
                <div class="button-container">
                  <el-tooltip class="item" effect="light" :content="getTooltipContent(scope.row)" placement="top">
                    <el-button type="info" icon="el-icon-view" class="operate"
                      :disabled="!disalbedResumeUpload(scope.row)" @click="openViewer(scope.row)"></el-button>
                  </el-tooltip>
                  <el-tooltip class="item" effect="light" :content="getTooltipContent2(scope.row)" placement="top">
                    <el-button type="success" icon="el-icon-download" class="operate"
                      :disabled="!disalbedResumeUpload(scope.row)" @click="downloadFile(scope.row)"></el-button>
                  </el-tooltip>
                  <el-tooltip lass="item" effect="light" content="删除" placement="top">
                    <el-button type="danger" icon="el-icon-delete" class="operate"
                      @click="recycleOrRecovery(scope.row)"></el-button>
                  </el-tooltip>
                </div>
              </div>
            </template>
          </el-table-column>
          <el-table-column prop="size" label="文件大小">
            <template slot-scope="scope">
              {{ formatFileSize(scope.row.size) }}
            </template>
          </el-table-column>
          <el-table-column label="状态">
            <template slot-scope="scope">
              <el-tag v-if="scope.row.isUploaded === 1" type="success">成功</el-tag>
              <el-tag v-if="scope.row.isUploaded === 0" type="warning">失败</el-tag>
            </template>
          </el-table-column>
        </el-table>
      </el-card>
    </div>
    <el-dialog :visible.sync="viewerDialogVisible" width="50%">
      <img v-if="viewerFileType === 'image'" :src="viewerFileUrl" style="width: 100%;" />
      <video v-else-if="viewerFileType === 'video'" :src="viewerFileUrl" controls style="width: 100%;"></video>
      <!-- 其他文件类型的预览 -->
      <div v-else>
        不支持的文件类型
      </div>
      <span slot="footer" class="dialog-footer">
        <el-button @click="viewerDialogVisible = false">关闭</el-button>
      </span>
    </el-dialog>
    <el-popover placement="right" width="400" trigger="click">
      <el-table :data="recycleList">
        <el-table-column prop="fileName" label="文件名">
          <template slot-scope="scope">
            <div class="file-name-container" @mouseover="showButtons(scope.$index)"
              @mouseleave="hideButtons(scope.$index)">
              {{ scope.row.fileName }}
              <div class="button-container">
                <el-tooltip class="item" effect="light" content="恢复" placement="top">
                  <el-button type="info" round icon="el-icon-refresh-left" class="operate"
                    @click="recycleOrRecovery(scope.row)"></el-button>
                </el-tooltip>
              </div>
            </div>
          </template>
        </el-table-column>
      </el-table>
      <el-button class="bucket" slot="reference">
      </el-button>
    </el-popover>
  </div>
</template>

<script>
import SparkMD5 from 'spark-md5';
import { Message, MessageBox } from 'element-ui';
import axios from 'axios';
import upload from "@/api/upload";
export default {

  data() {
    return {
      viewerDialogVisible: false,
      buttonShow: false,
      viewerFileType: '',
      viewerFileUrl: '',
      showUploadProgress: false,
      uploadingFileName: '',
      uploadProgress: 0,
      fileList: [],
      recycleList: [],
      file: null,
      chunkSize: 1024 * 1024 * 5, // 5MB
    };
  },
  created() {
    this.init()
  },
  methods: {
    recycleOrRecovery(row) {
      let message;
      if (row.isRecycle === 0)
        message = "确定要删除文件吗？"
      else
        message = "确定要恢复文件吗？"
      MessageBox.confirm(message, '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(() => {
        console.log("recycleOrRecovery");
        upload.recycleOrRecovery({ fileName: row.fileName, isRecycle: row.isRecycle })
          .then((res) => {
            if (res.code === 1)
              Message.success("成功")
            this.init()
          })
      }).catch(() => {
      });
    },
    getTooltipContent(row) {
      return row.isUploaded === 0 ? '请选择文件重新上传' : '预览';
    },
    getTooltipContent2(row) {
      return row.isUploaded === 0 ? '请选择文件重新上传' : '下载';
    },
    showButtons(index) {

      this.buttonShow = true;
    },
    hideButtons(index) {
      this.buttonShow = false;
    },
    init() {
      upload.fileList().then((res) => {
        this.fileList = res.response;
      });
      upload.recycleList().then((res) => {
        this.recycleList = res.response
      })
    },
    disalbedResumeUpload(row) {
      if (row.isUploaded === 1) {
        return true;
      }
      return false;
    },
    formatFileSize(sizeInBytes) {
      const sizes = ['Bytes', 'KB', 'MB', 'GB', 'TB'];

      if (sizeInBytes === 0) return '0 Byte';

      const i = parseInt(Math.floor(Math.log(sizeInBytes) / Math.log(1024)));

      return Math.round(sizeInBytes / Math.pow(1024, i), 2) + ' ' + sizes[i];
    },
    openViewer(row) {
      // 设置预览模态框的内容和显示状态
      if (row.isUploaded === 0) {
        Message.error('文件未上传完成');
        return;
      }
      console.log(row.fileUrl, "row.fileUrl");
      this.viewerFileType = this.getFileType(row.fileUrl);
      this.viewerFileUrl = row.fileUrl;
      this.viewerDialogVisible = true;
    },

    getFileType(url) {
      let strings = url.split('.');
      let type = strings[strings.length - 1];
      if (['pdf'].includes(type)) {
        return 'pdf';
      } else if (['jpg', 'jpeg', 'png', 'gif'].includes(type)) {
        return 'image';
      } else if (['mp4', 'avi', 'mov', 'ogg', 'webm'].includes(type)) {
        return 'video';
      }
    },


    downloadFile(row) {
      if (row.isUploaded === 0) {
        Message.error('文件未上传完成');
        return;
      }
      console.log("Downloading file:", row.fileUrl);
      // 创建一个虚拟的a标签并模拟点击来下载文件
      const link = document.createElement('a');
      link.href = row.fileUrl;
      link.download = row.fileName;
      link.target = '_blank';
      link.click();
    },

    handleSuccess() {
      upload.fileList().then((res) => {
        this.fileList = res.response
      });
    },
    handleChange(file) {
      console.log(file, "handleChange")
      console.log(this.fileList, "fileList")
      this.file = file.raw
    },
    async handleUpload() {
      if (!this.file) {
        Message.error('请选择文件');
        return;
      }
      this.showUploadProgress = true;
      this.uploadingFileName = this.file.name;
      try {
        const md5 = await this.calculateMD5(this.file);
        const chunks = Math.ceil(this.file.size / this.chunkSize);

        let uploadId;
        let uploadUrlList;

        const initResponse = await this.initUpload(md5);
        uploadId = initResponse.response.uploadId;
        uploadUrlList = initResponse.response.uploadUrlList;

        if (initResponse.response.url !== null) {
          Message.error('文件已存在', initResponse.response.url);
          this.showUploadProgress = false;
          return false;
        }

        await this.uploadChunks(uploadUrlList, uploadId);

        const mergeResponse = await this.mergeChunks(uploadId);

        if (mergeResponse.code === 1) {
          Message.success('上传成功', mergeResponse.url);
          this.showUploadProgress = false;
        } else {
          console.error('合并块失败');
          this.showUploadProgress = false;
        }
      } catch (error) {
        console.error('上传失败:', error);
        this.showUploadProgress = false;
      }
    },

    async initUpload(md5) {
      return await upload.init({ md5: md5, filename: this.file.name, size: this.file.size });
    },

    async uploadChunks(uploadUrlList, uploadId) {
      const totalChunks = uploadUrlList.length;
      let successfulChunks = 0;

      const promises = uploadUrlList.map(async (element, index) => {
        const url = `${process.env.VUE_APP_API_URL}${element}`;
        console.log(url, 'url');
        var match = url.match(/chunk_\d+/);
        const numericPart = match[0].split('_')[1];
        console.log(`开始获取分片 ${numericPart}`);

        const start = numericPart * this.chunkSize;
        const end = Math.min(start + this.chunkSize, this.file.size);
        const chunk = this.file.slice(start, end);

        const success = await this.uploadChunk(url, chunk, uploadId, numericPart);
        if (success) {
          successfulChunks++;
          this.uploadProgress = Math.floor((successfulChunks / totalChunks) * 100);
        }
        return success;
      });

      await Promise.all(promises);

      if (promises.some((p) => !p)) {
        console.log('有分片上传失败，取消合并');
        return false;
      } else {
        console.log('所有分片上传成功');
      }
    },
    async uploadChunk(url, chunk, uploadId, index) {
      const formData = new FormData();
      formData.append('file', chunk);

      try {
        const response = await axios.post(url, formData);
        if (response.data.code === 1) {
          console.log(`Chunk ${index} uploaded successfully`);
          return true;
        } else {
          console.log(`Chunk ${index} upload failed`);
          return false;
        }
      } catch (error) {
        console.error(`Error uploading chunk ${index}:`, error);
        return false;
      }
    },
    calculateMD5(file) {
      return new Promise((resolve, reject) => {
        const reader = new FileReader();
        const spark = new SparkMD5.ArrayBuffer();

        reader.onload = () => {
          spark.append(reader.result);
          resolve(spark.end());
        };

        reader.onerror = reject;

        reader.readAsArrayBuffer(file);
      });
    },
    async mergeChunks(uploadId) {
      return await upload.merge({ uploadId: uploadId });
    },
  },
};
</script>

<style scoped>
.el-card {
  margin: 30px auto;
}

.file-name-container {
  display: flex;
  align-items: center;
  justify-content: space-between;
  position: relative;
}


.operate {
  display: none;
  padding-right: 20px;
  margin-right: 20px;
  /* 根据需要调整边距 */
}

.el-table {
  min-height: 300px;
  /* 设置每行的最小高度 */
}

.file-name-container:hover .operate {
  display: inline-block;
}

.file-name-container {
  position: relative;
  display: flex;
  align-items: center;
  /* Center items vertically */
}

.button-container {
  position: absolute;
  top: 50%;
  right: 0;
  transform: translateY(-50%);

  visibility: hidden;
  opacity: 0;
  transition: visibility 0s, opacity 0.5s linear;
}

.file-name-container:hover .button-container {
  visibility: visible;
  opacity: 1;
}

.bucket {
  height: 50px;
  width: 50px;
  position: fixed;
  background-image: url('~@/icons/bucket.png');

  background-size: cover;

  right: 5%;
  top: 75%;
}
.upload-progress {
  transition: percentage 1.5s; /* 添加过渡效果，你可以根据需要调整过渡时间 */
}
</style>
