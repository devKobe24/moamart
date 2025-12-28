package com.kobe.moamart.dto.request;

import lombok.Data;

/**
 * packageName    : com.kobe.moamart.dto.request
 * fileName       : MemberJoinRequest
 * author         : kobe
 * date           : 2025. 12. 29.
 * description    :
 * ===========================================================
 * DATE              AUTHOR             NOTE
 * -----------------------------------------------------------
 * 2025. 12. 29.        kobe       최초 생성
 */
@Data
public class MemberJoinRequest {
    private String email;
    private String password;
    private String name;
    private String address;
}
