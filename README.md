### 项目名称
项目基于 MinioTemplate 类实现了简便的分片上传文件功能，支持秒传和断点续传。开箱即用。部署简单，可无缝移植。
本项目断点续传支持24小时内有效，超过24小时后，上传信息会被清除，需要重新上传。

新：

​	修改前端项目为Vue2+elementUI

​	文件上传提供进度条

​	新增文件列表（支持简单文件类型的预览、下载和回收）

​	回收站（7天后会自动清除）



#### 使用方法
  application.yml 进行基础配置minio redis mysql
  默认根据文件类型创建文件夹，可在FileUploadUtils自行配置



前端项目在文件  minio-multipart-web

#### 核心分片上传接口

1. 获取分片初始化信息 
		初始化上传流程，获取分片上传所需的信息。

``` json
请求体
{
"filename": "文件名称",
"md5": "文件内容的MD5值",
"size": "文件大小"
}
```

``` json
返回值:
{
"msg": "操作成功",
"code": 1,
"data": {
"uploadId": "936f4244-84d3-4d6c-8cff-cce0907bd823",
"uploadUrlList": [
"/api/file/multipart/chunkUpload?uploadId=936f4244-84d3-4d6c-8cff-cce0907bd823&chunk=chunk_0",
"/api/file/multipart/chunkUpload?uploadId=936f4244-84d3-4d6c-8cff-cce0907bd823&chunk=chunk_1",
"/api/file/multipart/chunkUpload?uploadId=936f4244-84d3-4d6c-8cff-cce0907bd823&chunk=chunk_2",
"/api/file/multipart/chunkUpload?uploadId=936f4244-84d3-4d6c-8cff-cce0907bd823&chunk=chunk_3",
"/api/file/multipart/chunkUpload?uploadId=936f4244-84d3-4d6c-8cff-cce0907bd823&chunk=chunk_4"
],
"partCount": 5,
"url": null
}
}
```



#### 2. 调用分片接口
   调用分片上传接口，将文件分片上传至服务器。
   http://localhost:8080/api/file/multipart/chunkUpload?uploadId=936f4244-84d3-4d6c-8cff-cce0907bd823&chunk=chunk_0
   http://localhost:8080/api/file/multipart/chunkUpload?uploadId=936f4244-84d3-4d6c-8cff-cce0907bd823&chunk=chunk_1
   http://localhost:8080/api/file/multipart/chunkUpload?uploadId=936f4244-84d3-4d6c-8cff-cce0907bd823&chunk=chunk_2
   http://localhost:8080/api/file/multipart/chunkUpload?uploadId=936f4244-84d3-4d6c-8cff-cce0907bd823&chunk=chunk_3
   http://localhost:8080/api/file/multipart/chunkUpload?uploadId=936f4244-84d3-4d6c-8cff-cce0907bd823&chunk=chunk_4
#### 3. 合并分片

	合并上传完成的分片，生成最终文件。

```json
请求体:
   {
   "uploadId": "936f4244-84d3-4d6c-8cff-cce0907bd823"
   }
```
 ``` json
  返回值:
 {
 "msg": "2023/02/10/91fc7d51-6caf-4d66-834e-165456f55975_20230210121947A001.mp4",
 "code": 1
 }
 ```
### 图片预览
![image-20210210122101810](img/img.png)
![image-20210210122101810](img/img_1.png)
![image-20210210122101810](img/img_2.png)
![image-20210210122101810](img/img_3.png)



