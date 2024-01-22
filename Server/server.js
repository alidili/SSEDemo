const express = require('express');
const http = require('http');
const app = express();
const server = http.createServer(app);

// 静态文件目录，发送消息使用
const path = require('path');
app.use(express.static(path.join(__dirname, 'public')));

// 用于存储连接的客户端响应对象
const clients = [];

// SSE长连接
app.get('/events', (req, res) => {
  // 设置响应头，指定事件流的Content-Type
  res.setHeader('Content-Type', 'text/event-stream; charset=utf-8');
  res.setHeader('Cache-Control', 'no-cache');
  res.setHeader('Connection', 'keep-alive');

  // 发送初始数据
  res.write('data: SSE 已连接\n\n');

  // 将客户端的响应对象存储起来
  clients.push(res);

  // 当连接断开时从数组中移除响应对象
  req.on('close', () => {
    clients.splice(clients.indexOf(res), 1);
  });
});

// 用于接收字符串类型的消息并发送给所有连接的客户端
app.post('/push', express.urlencoded({ extended: true }), (req, res) => {
  const message = req.body.message;

  // 向所有连接的客户端发送消息
  clients.forEach(client => {
    client.write(`data: 收到消息: ${message}，连接数：${clients.length}\n\n`);
  });

  res.status(200).send('Message sent successfully');
});

const PORT = process.env.PORT || 3000;
server.listen(PORT, () => {
  console.log(`Server listening on port ${PORT}`);
});
