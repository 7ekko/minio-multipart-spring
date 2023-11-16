项目名称
该项目基于 MinioTemplate 类实现了简便的分片上传文件功能，支持秒传和断点续传。

分片上传流程
1. 获取分片初始化信息
   初始化上传流程，获取分片上传所需的信息。

请求体:
{
"filename": "文件名称",
"md5": "文件内容的MD5值",
"size": "文件大小"
}
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
2. 调用分片接口
   调用分片上传接口，将文件分片上传至服务器。
http://localhost:8080/api/file/multipart/chunkUpload?uploadId=936f4244-84d3-4d6c-8cff-cce0907bd823&chunk=chunk_0
http://localhost:8080/api/file/multipart/chunkUpload?uploadId=936f4244-84d3-4d6c-8cff-cce0907bd823&chunk=chunk_1
http://localhost:8080/api/file/multipart/chunkUpload?uploadId=936f4244-84d3-4d6c-8cff-cce0907bd823&chunk=chunk_2
http://localhost:8080/api/file/multipart/chunkUpload?uploadId=936f4244-84d3-4d6c-8cff-cce0907bd823&chunk=chunk_3
http://localhost:8080/api/file/multipart/chunkUpload?uploadId=936f4244-84d3-4d6c-8cff-cce0907bd823&chunk=chunk_4
3. 合并分片
   合并上传完成的分片，生成最终文件。
请求体:
{
"uploadId": "936f4244-84d3-4d6c-8cff-cce0907bd823"
}
返回值:

{
"msg": "2023/02/10/91fc7d51-6caf-4d66-834e-165456f55975_20230210121947A001.mp4",
"code": 1
}
