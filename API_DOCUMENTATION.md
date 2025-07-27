# 歌曲功能API文档

## 新增功能概述

本次更新完善了歌曲功能，新增了以下四个主要功能：

1. **根据歌名模糊查询，分页**
2. **查询全部歌曲，分页**
3. **随机查询歌曲**
4. **根据歌曲风格分页查询**

## API接口详情

### 1. 根据歌名模糊查询（分页）

**接口地址：** `GET /song/searchByNameWithPage`

**请求参数：**
- `songName` (必填): 歌曲名称，支持模糊查询
- `pageNum` (可选): 页码，默认值为1
- `pageSize` (可选): 每页大小，默认值为10

**请求示例：**
```
GET /song/searchByNameWithPage?songName=爱情&pageNum=1&pageSize=10
```

**响应示例：**
```json
{
    "code": 1,
    "message": "查询成功",
    "data": {
        "data": [
            {
                "id": 1,
                "singerId": 1,
                "singerName": "张学友",
                "name": "爱情故事",
                "album": "吻别",
                "duration": "04:30",
                "style": "流行",
                "coverUrl": "cover1.jpg",
                "audioUrl": "audio1.mp3",
                "releaseTime": "1993-01-01"
            }
        ],
        "total": 1,
        "pageNum": 1,
        "pageSize": 10,
        "totalPages": 1
    }
}
```

### 2. 查询全部歌曲（分页）

**接口地址：** `GET /song/getAllSongsWithPage`

**请求参数：**
- `pageNum` (可选): 页码，默认值为1
- `pageSize` (可选): 每页大小，默认值为10

**请求示例：**
```
GET /song/getAllSongsWithPage?pageNum=1&pageSize=10
```

**响应示例：**
```json
{
    "code": 1,
    "message": "查询成功",
    "data": {
        "data": [
            {
                "id": 1,
                "singerId": 1,
                "singerName": "张学友",
                "name": "吻别",
                "album": "吻别",
                "duration": "04:30",
                "style": "流行",
                "coverUrl": "cover1.jpg",
                "audioUrl": "audio1.mp3",
                "releaseTime": "1993-01-01"
            }
        ],
        "total": 50,
        "pageNum": 1,
        "pageSize": 10,
        "totalPages": 5
    }
}
```

### 3. 随机查询歌曲

**接口地址：** `GET /song/getRandomSongs`

**请求参数：**
- `limit` (可选): 查询数量，默认值为20，最大值为100

**请求示例：**
```
GET /song/getRandomSongs?limit=20
```

**响应示例：**
```json
{
    "code": 1,
    "message": "查询成功",
    "data": [
        {
            "id": 1,
            "singerId": 1,
            "name": "吻别",
            "album": "吻别",
            "duration": "04:30",
            "style": "流行",
            "coverUrl": "cover1.jpg",
            "audioUrl": "audio1.mp3",
            "releaseTime": "1993-01-01"
        }
    ]
}
```

### 4. 根据风格分页查询

**接口地址：** `GET /song/searchByStyleWithPage`

**请求参数：**
- `style` (必填): 歌曲风格
- `pageNum` (可选): 页码，默认值为1
- `pageSize` (可选): 每页大小，默认值为10

**请求示例：**
```
GET /song/searchByStyleWithPage?style=流行&pageNum=1&pageSize=10
```

**响应示例：**
```json
{
    "code": 1,
    "message": "查询成功",
    "data": {
        "data": [
            {
                "id": 1,
                "singerId": 1,
                "singerName": "张学友",
                "name": "吻别",
                "album": "吻别",
                "duration": "04:30",
                "style": "流行",
                "coverUrl": "cover1.jpg",
                "audioUrl": "audio1.mp3",
                "releaseTime": "1993-01-01"
            }
        ],
        "total": 25,
        "pageNum": 1,
        "pageSize": 10,
        "totalPages": 3
    }
}
```

## 错误响应

当查询失败时，会返回错误信息：

```json
{
    "code": 0,
    "message": "错误信息",
    "data": null
}
```

## 分页响应格式

所有分页查询都返回 `PageResponse<T>` 格式，包含以下字段：

- `data`: 数据列表
- `total`: 总记录数
- `pageNum`: 当前页码
- `pageSize`: 每页大小
- `totalPages`: 总页数

## 测试页面

访问 `http://localhost:8080/song-test.html` 可以查看测试页面，用于验证所有新增功能。

## 数据库要求

确保数据库中的 `tb_song` 表包含以下字段：
- `id`: 主键
- `singer_id`: 歌手ID
- `name`: 歌曲名称
- `album`: 专辑
- `duration`: 时长
- `style`: 风格
- `cover_url`: 封面URL
- `audio_url`: 音频URL
- `release_time`: 发行时间

同时需要 `tb_singer` 表包含：
- `id`: 主键
- `singer_name`: 歌手名称 