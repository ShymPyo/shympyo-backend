package shympyo.user.domain;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter @Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Table(name="users")
public class User {

    @Id @GeneratedValue
    private Long id;

    @Column(nullable = false, unique = true, length = 100)
    private String email;

    @Column(nullable = true)
    private String password;

    @Column(nullable = false, length = 50)
    private String name;

    @Column(nullable = true, length = 50)
    private String nickname;

    @Column(length = 500)
    private String bio;

    @Column(nullable = true)
    private String phone;

    @Column(nullable = true)
    private String imageUrl;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private UserRole role;


    @PrePersist
    public void prePersist() {
        if (this.nickname == null) {
            this.nickname = name;
        }
        if (this.imageUrl == null) {
            this.imageUrl = "default_image_1";
        }
        if(this.bio == null){
            this.bio = "자기 소개를 입력해주세요";
        }
    }
}
