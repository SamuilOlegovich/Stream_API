package stream;




// https://annimon.com/article/2778 <--- тут все хорошо и наглядно описано
// https://www.youtube.com/watch?v=RzEiCguFZiY <--- тут видео
public class Main {

    public static void main(String[] args) {
        Streams streams = new Streams();

        streams.terminateStreams();
        streams.transformStreams();
        streams.creationStreams();
        streams.realStream();
    }
}
