package is.hi.hbv501g.nafnaneistar.nafnaneistar.Entities;

import javax.validation.constraints.NotNull;

public class Notification {
    @NotNull
    private String channelId;
    @NotNull
    private Long userId;
    private String message;

    public Notification(String cId, Long userId, String msg){
        this.channelId = cId;
        this.userId = userId;
        this.message = msg;
    }

    public String getChannelId() {
        return channelId;
    }

    public void setChannelId(String channelId) {
        this.channelId = channelId;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }


    
}
