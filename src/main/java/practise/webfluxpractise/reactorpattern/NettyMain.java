package practise.webfluxpractise.reactorpattern;

import java.util.List;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class NettyMain {
    public static void main(String[] args) {
        log.info("start main");
        List<EventLoop> eventLoop = List.of(new EventLoop(8080), new EventLoop(8081));
        eventLoop.forEach(EventLoop::run);
        log.info("end main");
    }

}
