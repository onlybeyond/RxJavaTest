package com.only.rxtest.common.model;

/**
 * Created by only on 16/6/15.
 * Email: onlybeyond99@gmail.com
 */
public class ServerResponse<T> {

    /**
     * error_code : 0
     * reason : Success
     * result : {"data":[{"content":"去女友老家提亲，女友安排我住宾馆，晚上突然听到敲门声：\u201c帅哥，需要特殊服务吗？\u201d我立刻拒绝了。\r\n然后接到女友电话：\u201c算你老实，通过了我家的测试。\u201d\r\n我愣了一下问道：\u201c刚那个人是？\u201d\r\n女友说：\u201c那是我哥。\u201d","hashId":"7e534cda97b5885533fc251453349f92","unixtime":1465739632,"updatetime":"2016-06-12 21:53:52"},{"content":"女友刚通过了路考，于是就让她开车带我去市里。停车场很挤，找到车位时，她发现要平行停车，一下子就紧张了起来。\r\n\u201c我相信你亲爱的，想想路考时你是怎么过的，再来一次就可以了！\u201d我鼓励她。于是她俯身慢慢拉开了我的裤子拉链。","hashId":"669886e3ebfccda6a1684da5cc604aec","unixtime":1465739632,"updatetime":"2016-06-12 21:53:52"},{"content":"刚刚女朋友找充电器充电，我告诉她在床底下，这货撅了半天屁股，\r\n本想去戳她一下，但还是没动手，毕竟哥是个有节操的人，\r\n结果这货充完电回头问我：\u201c你刚才咋没戳我屁股呢？我以为你肯定能干出来，等你好半天了。\u201d我俩果然是一对！","hashId":"c38b6333e7c6c03ca6b6564cd3313595","unixtime":1465739631,"updatetime":"2016-06-12 21:53:51"},{"content":"\u201c当我的钱包好可怜，一辈子都没怎么见过钱。\u201d\r\n神回复：\u201c当你的镜子才可怜，一辈子都没怎么见过人。\u201d","hashId":"6afb5f50d47a9153a76592d3b1f2ef4c","unixtime":1465739631,"updatetime":"2016-06-12 21:53:51"},{"content":"目前你最后悔的是什么？\r\n好奇，学会了网购。\r\n有最最后悔的事吗？\r\n显摆，教会了老婆。","hashId":"3a876c10a8cfcccbb839d56c8c125479","unixtime":1465739631,"updatetime":"2016-06-12 21:53:51"},{"content":"坡坡：我老婆从不主动跟我亲热，你说她是不是有病？\r\n兵兵：那你带她去检查一下呀。\r\n坡坡：我去了，厂家说，现在的技术，还达不到我说的那样\u2026\u2026","hashId":"fbe14e4dc35fdf3b170f848ecdea6ed8","unixtime":1465739631,"updatetime":"2016-06-12 21:53:51"},{"content":"某天和女友躺一起看电视，女友突然问我：\u201c你喜欢日本姑娘吗？\u201d\r\n我回答：\u201c当然不喜欢。\u201d\r\n其实我想说我只喜欢你，话还没说出，一巴掌过来了：\u201c不喜欢今晚睡沙发去\u201d。\r\n我瞬间凌乱了。","hashId":"99a458b13806bc90726afbfad27ddc0e","unixtime":1465738431,"updatetime":"2016-06-12 21:33:51"},{"content":"我：\u201c妈妈，人家都开始带胸罩了！我也想带了！\u201c\r\n妈妈：\u201c在等等吧！你还小！\u201c\r\n我：\u201c我不小了啊！我都快20了啊！\u201c\r\n妈妈：\u201c你丫的，我说的不是年龄！\u201c","hashId":"e9204d6b9d28f0654b700371f1f83959","unixtime":1465738431,"updatetime":"2016-06-12 21:33:51"},{"content":"理发馆碰一哥们，坐下后师傅问他洗不洗，他犹豫一下，答应了，并且选了洗发水，师傅很认真地把他头洗了两遍。\r\n回到座位上，师傅边为他擦干头发边问：\u201c打算理个什么头啊？\u201d\r\n这位仁兄对着镜子端详了半天，说：\u201c剃个光头吧。\u201d","hashId":"e68dc2d47d1e32044970800cf90c239d","unixtime":1465738431,"updatetime":"2016-06-12 21:33:51"},{"content":"我带儿子到附近的一家超市购物。在化妆品货架旁，儿子指着一瓶增白霜说：\u201c妈，给我买一瓶增白霜抹抹我的手，增增白！\u201d\r\n我惊奇地问：\u201c你一个男孩子为什么要那么白的手？\u201d\r\n儿子说：\u201c因为你说过我长大了，一切都要\u2018白手起家\u2019！\u201d","hashId":"e3f855bb29599ed0686293575670a0a8","unixtime":1465738431,"updatetime":"2016-06-12 21:33:51"}]}
     */

    private int error_code;
    private String reason;
    private T result;



    public int getError_code() {
        return error_code;
    }

    public void setError_code(int error_code) {
        this.error_code = error_code;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public T getResult() {
        return result;
    }

    public void setResult(T result) {
        this.result = result;
    }
}
