package hello.item_service.web.basic;

import hello.item_service.domain.item.Item;
import hello.item_service.domain.item.ItemRepository;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/basic/items")
@RequiredArgsConstructor // final 이 붇으면 생성자에서 생성자 주입을 자동으로 해준다
public class BasicItemController {
    private final ItemRepository itemRepository;

    /**
     * 아이템 목록 items.html 을 랜더링하는 컨트롤러
     * Model 을 사용해서 DispatcherServlet 에 넘겨주기
     */
    @GetMapping
    public String items(Model model){
        List<Item> items = itemRepository.findAll();
        model.addAttribute("items", items);
        //Error resolving template [basic/item]<-이 template이 존재하지 않는다, template might not exist
        return "basic/items"; //논리적 경로
    }

    @GetMapping("/{itemId}")
    public String item(@PathVariable long itemId, Model model){
        Item item = itemRepository.findById(itemId);
        model.addAttribute("item", item);
        return "basic/item";
    }
    @GetMapping("/add")
    public String addForm(){
        return "basic/addForm";
    }

    /**
     * 상품등록 화면에서 form 태그의 post 방식으로 넘어온후
     * 상품등록화면과 동일한 URL사용 but http메서드로 구분
     */
    @PostMapping("/add")
    public String save(){
        return "basic/addForm";
    }
    /**
     * 테스트 용
     */
    @PostConstruct
    public void init(){
        itemRepository.save(new Item("itemA", 10000, 10));
        itemRepository.save(new Item("itemB", 20000, 20));

    }
}
