package com.wiam.lms.domain;

import java.util.Random;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicLong;

public class UserCustomTestSamples {

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    public static UserCustom getUserCustomSample1() {
        return new UserCustom()
            .id(1L)
            .firstName("firstName1")
            .lastName("lastName1")
            .email("email1")
            .accountName("accountName1")
            .password("password1")
            .phoneNumber1("phoneNumber11")
            .phoneNumver2("phoneNumver21")
            .countryInternalId(1L)
            .nationalityId(1L)
            .address("address1")
            .facebook("facebook1")
            .telegramUserCustomId("telegramUserCustomId1")
            .telegramUserCustomName("telegramUserCustomName1")
            .biography("biography1")
            .bankAccountDetails("bankAccountDetails1")
            .jobInternalId(1L);
    }

    public static UserCustom getUserCustomSample2() {
        return new UserCustom()
            .id(2L)
            .firstName("firstName2")
            .lastName("lastName2")
            .email("email2")
            .accountName("accountName2")
            .password("password2")
            .phoneNumber1("phoneNumber12")
            .phoneNumver2("phoneNumver22")
            .countryInternalId(2L)
            .nationalityId(2L)
            .address("address2")
            .facebook("facebook2")
            .telegramUserCustomId("telegramUserCustomId2")
            .telegramUserCustomName("telegramUserCustomName2")
            .biography("biography2")
            .bankAccountDetails("bankAccountDetails2")
            .jobInternalId(2L);
    }

    public static UserCustom getUserCustomRandomSampleGenerator() {
        return new UserCustom()
            .id(longCount.incrementAndGet())
            .firstName(UUID.randomUUID().toString())
            .lastName(UUID.randomUUID().toString())
            .email(UUID.randomUUID().toString())
            .accountName(UUID.randomUUID().toString())
            .password(UUID.randomUUID().toString())
            .phoneNumber1(UUID.randomUUID().toString())
            .phoneNumver2(UUID.randomUUID().toString())
            .countryInternalId(longCount.incrementAndGet())
            .nationalityId(longCount.incrementAndGet())
            .address(UUID.randomUUID().toString())
            .facebook(UUID.randomUUID().toString())
            .telegramUserCustomId(UUID.randomUUID().toString())
            .telegramUserCustomName(UUID.randomUUID().toString())
            .biography(UUID.randomUUID().toString())
            .bankAccountDetails(UUID.randomUUID().toString())
            .jobInternalId(longCount.incrementAndGet());
    }
}
