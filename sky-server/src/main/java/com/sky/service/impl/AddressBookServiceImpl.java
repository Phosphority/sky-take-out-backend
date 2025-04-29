package com.sky.service.impl;

import com.sky.constant.JwtClaimsConstant;
import com.sky.context.BaseContext;
import com.sky.entity.AddressBook;
import com.sky.mapper.AddressBookMapper;
import com.sky.service.AddressBookService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

@Service
public class AddressBookServiceImpl implements AddressBookService {

    @Resource
    private AddressBookMapper addressBookMapper;

    @Override
    public void add(AddressBook addressBook) {
        addressBook.setUserId(BaseContext.getCurrentId().get(JwtClaimsConstant.USER_ID));
        addressBookMapper.add(addressBook);
    }

    @Override
    public AddressBook list(Long userId) {
        return addressBookMapper.list(userId);
    }

    @Override
    public void delete(Long id) {
        addressBookMapper.delete(id);
    }

    @Override
    public void setDefault(Long id) {
        Long userId = BaseContext.getCurrentId().get(JwtClaimsConstant.USER_ID);
        addressBookMapper.setDefault(id,userId);
    }

    @Override
    public void getDefault() {
        Long userId = BaseContext.getCurrentId().get(JwtClaimsConstant.USER_ID);
        addressBookMapper.getDefault(userId);
    }

    @Override
    public void update(AddressBook addressBook) {
        addressBookMapper.update(addressBook);
    }
}
