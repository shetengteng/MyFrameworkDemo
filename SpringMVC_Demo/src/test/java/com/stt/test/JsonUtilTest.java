package com.stt.test;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

import com.fasterxml.jackson.core.type.TypeReference;
import com.stt.pojo.Account;
import com.stt.pojo.Result;
import com.stt.utils.JsonUtil;

public class JsonUtilTest extends BaseTest {

	@Test
	public void test01() {

		Result<Account> result = new Result<Account>();

		Account a = new Account("stt");
		result.setInfo("OK");
		List<Account> items = new ArrayList<>();
		items.add(a);
		result.setItems(items);
		String jsonStr = JsonUtil.toJsonString(result);
		System.out.println(jsonStr);
		System.out.println("-----------");
		Result<Account> toObject1 = JsonUtil.toObject(jsonStr, Result.class);

		Result<Account> toObject2 = JsonUtil.toObject(jsonStr,
				new TypeReference<Result<Account>>() {
				});

		// 有输出：Result [info=OK, items=[{id=0, name=stt, score=null}]]
		System.out.println(toObject1);
		// ERROR:这里报错，转换异常
		// System.out.println(toObject1.getItems().get(0).getName());
		System.out.println("-----------");
		System.out.println(toObject2);
		// 转换正常：输出 Result [info=OK, items=[Account [id=0, name=stt]]]
		System.out.println(toObject2.getItems().get(0).getName());

		// 总结，如果有泛型类别，则需要使用 TypeReference 来引用泛型
		// 如 HashMap<String,Account> ,如果要转换为对应的Map，则必须使用 TypeReference
	}

	@Test
	public void testToObjects() {
		List<Account> list = new ArrayList<>();
		Account a = new Account();
		a.setId(1);
		a.setName("ss");
		list.add(a);

		Account b = new Account();
		b.setId(0);
		b.setName("rr");
		b.setScore(90D);
		list.add(b);

		String jsonString = JsonUtil.toJsonString(list);
		System.out.println(jsonString);

		List<Account> list2 = JsonUtil.toObjects(jsonString, Account.class);
		for (Account account : list2) {
			System.out.println(account);
		}

	}
}
