public class Messages {
    enum MsgType{
        MyNameMsg,
        InitMsg,
        BoardMsg,
        StartMsg,
        UpdPlMsg,
        UpdCardMsg,
        BuildMsg,
        BuyMsg,
        TradeMsg,
        MoveMsg,
        UserMsg,
        ServerMsg,
        EndMsg,
        ExitMsg,
        WinMsg;
    }
    public static class MyNameMsg{
        public String name;
        public MsgType type = MsgType.MyNameMsg;
    }
    public static class InitMsg{
        public String name0;
        public String name1;
        public String name2;
        public String name3;
        public MsgType type = MsgType.InitMsg;
    }
    public static class MoveMsg {
        public int idPlayer;
        public int Dice1, Dice2;
        public MsgType type = MsgType.MoveMsg;
    }
    public static class UpdCardMsg {
        public int cardID;
        public int owner;
        public boolean isFree;
        public int houseCount;
        public int hotelCount;
        public MsgType type = MsgType.UpdCardMsg;
    }
    public static class UpdPlMsg{
        public String name;
        public int id;
        public int money;
        public int position;
        public MsgType type = MsgType.UpdPlMsg;
    }
    public static class BuildMsg{
        public int idCard;
        public int  idPlayer;
        public MsgType type = MsgType.BuildMsg;
    }
    public static class BuyMsg{
        public int idCard;
        public int  idPlayer;
        public MsgType type = MsgType.BuyMsg;
    }
    public static class TradeMsg{
        public int idFrom;
        public int idTo;
        public int[] cardsFrom;
        public int[] cardsTo;
        public int moneyFrom;
        public int moneyTo;
        public MsgType type = MsgType.TradeMsg;
        public int response;//-1 - ожидает ответа 0 - нет 1 - да
    }
    public static class StartMsg{
        public int activeId;
        public MsgType type = MsgType.StartMsg;
    }
    public static class EndMsg {
        public int your_id;
        public MsgType type = MsgType.EndMsg;
    }
    public static class UserMsg{
        public String userName;
        public String msg;
        public MsgType type = MsgType.UserMsg;
    }
    public static class ServerMsg{
        public String msg;
        public MsgType type = MsgType.ServerMsg;
    }
    public static class ExitMsg{
        public int id;
        public MsgType type = MsgType.ExitMsg;
    }
    public static class WinMsg{
        public int id;
        public MsgType type = MsgType.WinMsg;
    }
}
