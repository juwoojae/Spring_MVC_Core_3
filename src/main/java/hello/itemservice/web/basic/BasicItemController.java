package hello.itemservice.web.basic;

import hello.itemservice.domain.item.DeliveryCode;
import hello.itemservice.domain.item.Item;
import hello.itemservice.domain.item.ItemRepository;
import hello.itemservice.domain.item.ItemType;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Controller
@RequestMapping("/basic/items")
@RequiredArgsConstructor // final 이 붇으면 생성자에서 생성자 주입을 자동으로 해준다
public class BasicItemController {

    private final ItemRepository itemRepository;

    /**
     * ModelAttribute 의 2번째 기능
     * 모든 컨트롤러 클래스의 메서드에 해당 return 값을
     * ("regions", regions) Model 에 넣어준다
     */
    @ModelAttribute("regions")
    public Map<String, String> regions(){
        Map<String, String> regions = new LinkedHashMap<>();
        regions.put("SEOUL", "서울");
        regions.put("BUSAN", "부산");
        regions.put("JEJU", "제주");
        return regions;
    }
    @ModelAttribute("itemTypes")
    public ItemType[] itemTypes(){
        return ItemType.values(); //ENUM 의 values() ENUM 형의 배열을 리턴
    }

    @ModelAttribute("deliveryCodes")
    public List<DeliveryCode> deliveryCodes() {
        List<DeliveryCode> deliveryCodes = new ArrayList<>();
        deliveryCodes.add(new DeliveryCode("FAST", "빠른 배송"));
        deliveryCodes.add(new DeliveryCode("NORMAL", "일반 배송"));
        deliveryCodes.add(new DeliveryCode("SLOW", "느린 배송"));
        return deliveryCodes;
    }
    /**
     * 아이템 목록 items.html 을 랜더링하는 컨트롤러
     * Model 을 사용해서 DispatcherServlet 에 넘겨주기
     */
    @GetMapping
    public String items(Model model) {
        List<Item> items = itemRepository.findAll();
        model.addAttribute("items", items);
        //Error resolving template [basic/item]<-이 template이 존재하지 않는다, template might not exist
        return "basic/items"; //논리적 경로
    }

    /**
     * items 리스트에서 id 를 URL get 방식으로 받았다
     * basic/items/itemId 호출
     * itemId 를 @PathVariable 을 사용해서 가져오기
     * 이 itemId 를 가지고 item 객체 찾기
     * model에 넘겨주기 -> 다음 요청에서 사용한다
     */
    @GetMapping("/{itemId}")
    public String item(@PathVariable long itemId, Model model) {
        Item item = itemRepository.findById(itemId);
        model.addAttribute("item", item);
        return "basic/item";
    }

    @GetMapping("/add")
    public String addForm(Model model) {
        model.addAttribute("item", new Item());
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
                            Model model) {
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
    public String addItemV2(@ModelAttribute("item") Item item) {
        itemRepository.save(item);

        return "basic/item";
    }

    /**
     * @ModelAttribute() 의 파라메터를 생략해서 넘겨주는 경우
     * 클래스 Item -> item 으로 앞글자만 소문자로 바꾼뒤 이것을 넘겨준다
     */
    //@PostMapping("/add")
    public String addItemV3(@ModelAttribute() Item item) {
        itemRepository.save(item);

        return "basic/item";
    }

    //@PostMapping("/add")
    public String addItemV4(Item item) {
        itemRepository.save(item);

        return "basic/item";
    }//PRG 리펙토링

    /**
     * @ModelAttribute() 이거 자체도 생략 가능하다
     * 클래스 Item -> item 으로 앞글자만 소문자로 바꾼뒤 이것을 넘겨준다
     * 새로고침 : 내가 마지막에 했던  요청을 한번 더한다
     * PRG Post/Redirect/Get
     * 만약 내가 Post /add 호출후 basic/item 렌더링 화면에서 새로고침을 하면
     * Post /add 를 한번더 호출하게 된다
     * Post /add 호출후 Redirect/items/{id} 상세 조회 페이지로 Redirect ->Get /items/{id} 호출 ->상세조회 페이지 렌더링
     * 여기서 새로 고침을 하면 마지막 요청은 Get /items/{id} 이므로 이게 요청이 된다!!
     */
    //@PostMapping("/add")
    public String addItemV5(Item item) {
        itemRepository.save(item);

        return "redirect:/basic/items/" + item.getId();
    }

    /**
     * 상품 저장시 상품 상세 화면에 "저장 되었습니다" 라는 메세지를 보여달라는 요구사항 해결
     * RedirectAttributes 를 사용하면 URL 인코딩도 해주고, pathVarible, 쿼리 파라미터까지 처리해준다
     * redirect:/basic/{itemId}
     * pathVariable 바인딩
     * 나머지는 쿼리 파라미터로 처리
     */
    @PostMapping("/add")

    public String addItemV6(Item item, RedirectAttributes redirectAttributes) {

        log.info("item.open={}", item.getOpen());// lombok 의 getter
        log.info("item.regions={}", item.getRegions());
        log.info("item.ItemType={}", item.getItemType());

        Item savedItem = itemRepository.save(item); //넘어온 form data 들을 모두 itemRepository 에 저장
        redirectAttributes.addAttribute("itemId", savedItem.getId());
        redirectAttributes.addAttribute("status", true);
        return "redirect:/basic/items/{itemId}";
    }

    /**
     * 상품 수정 폼
     */
    @GetMapping("/{itemId}/edit")
    public String editForm(@PathVariable Long itemId, Model model) {
        Item item = itemRepository.findById(itemId);
        model.addAttribute("item", item);
        return "basic/editForm";
    }

    /**
     * 상품 수정 처리
     * 같은 URL 에 form 태그로 Post 로 들어오면 여기서 매핑
     *
     * @PathVariable 로 id경로 받아오기
     * @ModelAttribute 로 요청 파라메터에서 값을 모두 꺼낸후 item 객체에 넣기
     * 이것을 Model에 model.addAttribute("item",item);으로 넣기
     * 그후 redirect로 /basic/items/{itemId} 상세 정보페이지 요청 -> 매핑후 ->응답(화면 렌더링)
     */
    @PostMapping("/{itemId}/edit")
    public String edit(@PathVariable Long itemId, @ModelAttribute Item item) {
        itemRepository.update(itemId, item);
        return "redirect:/basic/items/{itemId}"; //다시 URL 을 요청할것을 응답
    }

    /**
     * 테스트 용
     */
    @PostConstruct
    public void init() {
        itemRepository.save(new Item("itemA", 10000, 10));
        itemRepository.save(new Item("itemB", 20000, 20));

    }
}
