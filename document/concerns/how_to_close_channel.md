# 如何关闭 channel
## 交互模式（上下文/场景）
在通信过程中，response 与 request 一对一对应，每个 request 都应当有对应的 response。

## 关闭 channel 的时机
### server
没有 pending request（或者说未处理的 request） 后，即可关闭。

可以根据情况，保留 channel 一段时间，以备 client 再发请求。

### client
得到想知道的所有问题（requests）的答案后（responses），即可关闭 channel。

可保留 channel 一小段时间，以备用户触发新的 request。

## 对方关闭其 outputstream（即己方 inputstream）时的应对
### server
不必再保留 channel 一段时间。

没有 pending request（或者说未处理的 request） 后，即可立即关闭 channel。

### client
立即关闭 channel。

对于未应答的 request，根据具体 request 做恰当处理。通知用户未完成的任务。

未应答的 request 包括：
* server 没有给出相应 response 的已发 request
* 未来得及发的 request

## 对方关闭 channel（inputstream 和 outputstream） 时的应对
### server
log

立即关闭 channel。

### client
同上一节
