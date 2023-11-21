import { post ,put} from '@/utils/request'

export default {
  init: query => post('/api/file/multipart/init', query),
  fileList: () => post('/api/file/load'),
  // 回收站列表
  recycleList: () => post('/api/file/recycle'),
  // 回收或者恢复
  recycleOrRecovery : query => post('/api/file/recycleOrRecovery',query),
  // 合并
  merge: query => put('/api/file/multipart/complete', query)
}
