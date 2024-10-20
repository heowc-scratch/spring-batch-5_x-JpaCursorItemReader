package dev.heowc;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

@Entity
public class User {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private long id;
  private String nickname;
  private String nickname2;
  private String nickname3;

  protected User() {
  }

  public User(String nickname) {
    this.nickname = nickname;
    this.nickname2 = nickname.repeat(2);
    this.nickname3 = nickname.repeat(3);
  }

  public long getId() {
    return id;
  }

  public String getNickname() {
    return nickname;
  }

  public String getNickname2() {
    return nickname2;
  }

  public String getNickname3() {
    return nickname3;
  }

  @Override
  public String toString() {
    return "User{" +
        "id=" + id +
        ", nickname='" + nickname + '\'' +
        '}';
  }
}
