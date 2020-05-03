package study.datajpa.entity;

import lombok.*;

import javax.persistence.*;

@Entity
@Getter @Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@ToString(of = {"id", "username", "age"})   // Team과같은 연관관계 field는 toString() X!!!
public class Member {

    @Id @GeneratedValue
    @Column(name = "member_id")
    private Long id;
    private String username;
    private int age;

    @ManyToOne(fetch = FetchType.LAZY)  // @XToOne 관계에서 default 값은 EAGER 이므로 LAZY로 바꿔줘야함
    @JoinColumn(name = "team_id")  // 1:N 관계에서는 N쪽에 외래키 걸어줌
    private Team team;

    // 생성자
    public Member(String username) {
        this.username = username;
    }

    public Member(String username, int age, Team team) {
        this.username = username;
        this.age = age;
        if(team != null) {
            changeTeam(team);
        }
    }

    /**
     *  연관관계 change 하는 메소드
     */
    public void changeTeam(Team team) {
        this.team = team;
        team.getMembers().add(this); // 내 것만 바꾸는 것이 아니라, Team에 가서도 add 해줘야함
    }
}
