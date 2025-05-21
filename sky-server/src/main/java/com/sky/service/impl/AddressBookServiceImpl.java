package com.sky.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.sky.constant.JwtClaimsConstant;
import com.sky.context.BaseContext;
import com.sky.entity.AddressBook;
import com.sky.mapper.AddressBookMapper;
import com.sky.service.AddressBookService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

@Service
public class AddressBookServiceImpl extends ServiceImpl<AddressBookMapper,AddressBook> implements AddressBookService {

    @Resource
    private AddressBookMapper addressBookMapper;

    @Override
    public void add(AddressBook addressBook) {
        addressBook.setUserId(BaseContext.getCurrentId().get(JwtClaimsConstant.USER_ID));
        addressBookMapper.add(addressBook);
    }

    @Override
    public List<AddressBook> list(Long userId) {
        return addressBookMapper.list(userId);
    }

    @Override
    public void delete(Long id) {
        addressBookMapper.delete(id);
    }

    @Override
    public void updateDefault(Long id) {
        Long userId = BaseContext.getCurrentId().get(JwtClaimsConstant.USER_ID);

        // 先将地址簿中该用户的所有地址都设为非默认地址
        AddressBook addressBook = AddressBook.builder()
                .userId(userId)
                .isDefault(0)
                .build();
        addressBookMapper.update(addressBook);

        // 然后直接将这个设置为默认地址即可
        addressBook.setId(id);
        addressBook.setIsDefault(1);
        addressBookMapper.update(addressBook);
    }

    @Override
    public AddressBook getDefault() {
        Long userId = BaseContext.getCurrentId().get(JwtClaimsConstant.USER_ID);
        return addressBookMapper.getDefault(userId);
    }

    @Override
    public void update(AddressBook addressBook) {
        addressBookMapper.update(addressBook);
    }

    @Override
    public AddressBook findById(Long id) {
        return addressBookMapper.findById(id);
    }
}
