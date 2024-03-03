package com.example.demo.domain;

import java.time.LocalDateTime;
import java.util.Objects;

public class FriendRequest extends Entity<Tuple<Long,Long>>{
    private FriendRequestStatus status;

    public FriendRequest(FriendRequestStatus status) {
        this.status = status;
    }

    public FriendRequestStatus getStatus() {
        return status;
    }

    public void setStatus(FriendRequestStatus status) {
        this.status = status;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        FriendRequest that = (FriendRequest) o;
        return Objects.equals(status, that.status) ;
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), status);
    }

    @Override
    public String toString() {
        return "FriendRequest{" +
                "status='" + status + '\'' +
                ", id=" + id +
                '}';
    }
}
