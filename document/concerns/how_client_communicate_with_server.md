# 客户端与服务器如何通信
## 模式
一个客户端使用单个信道（channel）与服务器连接。信道复用，用其传送各种/所有消息。

客户端发 request 给服务器，服务器对应地发 response，回应客户端。response 与 request 一一对应。

一个 channel 中可以有多个会话（session）。逻辑上，在一个会话中做一件事，可以包含多个步骤（step），step 是有序的，通信双方的应答有顺序，即服务器端以 requests 的顺序发送 responses。

不同会话下的 requests、 responses 可以交叉。session1 和 seesion2 的 requests 可以交叉，responses 同样如此；session1 先于 seesion2 发的request，可以晚于 session2 得到应答。设计动机：1. 进行简单任务的 session 可以早得到回复；2. 便于并发。

**注意**：服务器应当可以同时连接多个 channels 或者说 clients。从同一个 IP 来的多个 channels ，可以视为来自不同的 clients。

## 例子
### 一个会话的例子

| client | channel | server |
|--- | --- | ---|
| request1 | -> | |
| request2 | -> | |
| | | process1 |
| | <- | response1 |
| | | process2 |
| | <- | response2 |

一个会话中:
* 一个 request 对应一个 response。
* response 顺序与 request 顺序一致

### 一个 channel 中通信的例子

| client | channel | server |
|--- | --- | ---|
| (session1, request1) | -> | |
| (session2, request1) | -> | |
| (session1, request2) | -> | |
| | | (session1, process1), (session2, process1) |
| | <- | (session1, response1) |
| | | (session1, process2), (session2, process1) |
| | <- | (session1, response2) |
| | | (session2, process1) |
| | <- | (session2, response1) |

一个信道中:
* 一个 request 对应一个 response。
* session2 的 request1 可以插入 session1 的两个 requests 中间
* session2 的 request1 需要服务器的长期运算才可得到答案， 它对应的 (session2, response1) 可以晚于 (session1, request2) 的应答 (session1, response2)，虽然 (session2, request1) 早于 (session1, request2) 发送
