package jpabook.jpashop.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import jpabook.jpashop.domain.item.Book;
import jpabook.jpashop.domain.item.Item;
import jpabook.jpashop.repository.ItemRepository;
import lombok.RequiredArgsConstructor;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class ItemService {

	private final ItemRepository itemRepository;
	
	@Transactional
	public void saveItem(Item item) {
		itemRepository.save(item);
	}
	
	@Transactional
	public void updateItem(Long itemId, int price, String name ,int stockQuantity) {
		
		//dirty checking(변경감지)
		//준영속성 엔티티 상태를 영속성 상태로 바꿔줌
		//-> JPA 영속성 컨텍스트에서 관리되어 변경감지 일어남
		Item findItem = itemRepository.findOne(itemId);
		findItem.setName(name);
		findItem.setPrice(price);
		findItem.setStockQuantity(stockQuantity);
		
	}
	
	public List<Item> findItems() {
		return itemRepository.findAll();
	}
	
	public Item findItem(Long itemId) {
		return itemRepository.findOne(itemId);
	}
}
