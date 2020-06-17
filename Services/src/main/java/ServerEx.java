public class ServerEx extends Exception {

        public ServerEx(){}

        public ServerEx(String message) {
            super(message);
        }

        public ServerEx(String message, Throwable cause){
            super(message, cause);
        }
    }


