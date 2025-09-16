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
//    @PostMapping("/add")
    public String addItemV1(@RequestParam String itemName,
                       @RequestParam int price,
                       @RequestParam Integer quantity,
                       Model model){
        Item item = new Item();
        item.setItemName(itemName);
        item.setPrice(price);
        item.setQuantity(quantity);

        itemRepository.save(item);

        model.addAttribute("item", item);

        return "basic/item";
    }
    /**
     * @ModelAttribute("model에 넘겨줄 이 름")
     * 1. Item 객체 생성
     * 2. 요청 파라메터의 이름으로 Item 객체의 프로퍼티(getter,setter)를 찾는다, 그리고 해당 프로퍼티의 setter
     * 를 호출해서 파라미터의 값을 입력(바인딩) 한다.
     * 3. Model 에 담기(View 랜더링 시 필요)
     * 자동으로 model.addAttribute("Item", item) 를 호출해 뷰에서 item 를 바로 쓸수 있도록 Model 에 넣어 준다
     * 하지만 @ResponseBody 는 뷰가 필요 없기때문에 사실상 3번은 생략
     */
    //@PostMapping("/add")
    public String addItemV2(@ModelAttribute("item") Item item){
        itemRepository.save(item);

        return "basic/item";
    }

    /**
     * @ModelAttribute() 의 파라메터를 생략해서 넘겨주는 경우
     * 클래스 Item -> item 으로 앞글자만 소문자로 바꾼뒤 이것을 넘겨준다
     */
    @PostMapping("/add")
    public String addItemV3(@ModelAttribute() Item item){
        itemRepository.save(item);

        return "basic/item";
    }

    /**
     * @ModelAttribute() 이거 자체도 생략 가능하다
     * 클래스 Item -> item 으로 앞글자만 소문자로 바꾼뒤 이것을 넘겨준다
     */
    @PostMapping("/add")
    public String addItemV4(Item item){
        itemRepository.save(item);

        return "basic/item";
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
