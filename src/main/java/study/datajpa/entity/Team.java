package study.datajpa.entity;

import lombok.*;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter @Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@ToString(of = {"id", "name"})
public class Team extends BaseTimeEntity{

    @Id @GeneratedValue
    @Column(name = "team_id")
    private Long id;
    private String name;

    @OneToMany(mappedBy = "team")  // foreign 키 없는쪽에 mapped by 걸어줌
    private List<Member> members = new ArrayList<>();

    public Team(String name) {
        this.name = name;
    }
}
