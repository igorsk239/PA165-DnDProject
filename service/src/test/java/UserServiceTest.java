import cz.fi.muni.PA165.api.exceptions.DnDServiceException;
import cz.fi.muni.PA165.persistence.dao.HeroDao;
import cz.fi.muni.PA165.persistence.dao.UserDao;
import cz.fi.muni.PA165.persistence.model.Hero;
import cz.fi.muni.PA165.persistence.model.User;
import cz.fi.muni.PA165.service.UserService;
import cz.fi.muni.PA165.service.UserServiceImpl;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.util.List;
import java.util.Set;

import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;

/**
 * Tests for User service
 *
 * @author Alena Bednarikova
 */

public class UserServiceTest {
    private UserService userService;

    @Mock
    private UserDao userDao;

    @Mock
    private HeroDao heroDao;

    private User user1;

    private Hero hero;

    @BeforeMethod
    public void init(){
        MockitoAnnotations.initMocks(this);
        userService = new UserServiceImpl(userDao, heroDao);

        hero = new Hero();
        hero.setId(1L);
        hero.setLevel(1);
        hero.setName("HeroName");

        user1 = new User();
        user1.setId(1L);
        user1.setUserName("UserName");
        user1.setPasswordHash("fdslfkjsdgjfj565sdsdf");
        user1.setAdmin(true);
        user1.addHero(hero);
    }

    @Test
    public void findById(){
        given(userDao.findById(user1.getId())).willReturn(user1);
        User user = userDao.findById(user1.getId());
        assertEquals(user, user1);
        then(userDao).should().findById(user1.getId());
    }

    @Test(expectedExceptions = DnDServiceException.class)
    public void findByIdWithException(){
        given(userDao.findById(anyLong())).willReturn(null);
        userService.findById(60L);
    }

    @Test
    public void createUser(){
        userService.createUser(user1, "dkfhsjdkhkdfh554dsfdf");
        then(userDao).should().create(user1);
    }

    @Test
    public void updateUser(){
        given(userDao.findById(user1.getId())).willReturn(user1);
        userService.updateUser(user1);
        then(userDao).should().update(user1);
    }

    @Test
    public void deleteUser(){
        given(userDao.findById(user1.getId())).willReturn(user1);
        userService.deleteUser(user1.getId());
        then(userDao).should().delete(user1);
    }

    @Test
    public void findAllUsers(){
        User user2 = new User();
        user2.setUserName("user2");
        user2.setPasswordHash("mxclklkslksd2121sfdg21xchjs");
        user2.setAdmin(false);
        given(userDao.findAll()).willReturn(List.of(user1, user2));
        List<User> users = userService.findAllUsers();
        assertEquals(users.size(), 2);
        assertTrue(users.contains(user1));
        assertTrue(users.contains(user2));
        then(userDao).should().findAll();
    }

    @Test
    public void addHeroToUser(){
        given(userDao.findById(user1.getId())).willReturn(user1);
        Set<Hero> heroes = user1.getHeroes();
        assertEquals(heroes.size(), 1);
        assertTrue(heroes.contains(hero));

        Hero hero2 = new Hero();
        hero2.setId(2L);
        hero2.setName("Hero2Name");
        given(heroDao.findById(hero2.getId())).willReturn(hero2);
        userService.addHeroToUser(hero.getId(), hero2.getId());

        heroes = user1.getHeroes();
        assertEquals(heroes.size(), 2);
        assertTrue(heroes.contains(hero));
        assertTrue(heroes.contains(hero2));

    }
    @Test
    public void removeHero(){
        given(userDao.findById(user1.getId())).willReturn(user1);
        given(heroDao.findById(hero.getId())).willReturn(hero);
        userService.removeHeroFromUser(user1.getId(), hero.getId());
        assertEquals(user1.getHeroes().size(), 0);
    }

    @Test(expectedExceptions = DnDServiceException.class)
    public void removeHeroWhenHeroIdIsWrong(){
        given(userDao.findById(user1.getId())).willReturn(user1);
        userService.removeHeroFromUser(hero.getId(), 54L);
        assertEquals(user1.getHeroes().size(), 0);
    }

    @Test(expectedExceptions = DnDServiceException.class)
    public void removeHeroWhenUserIdIsWrong(){
        given(heroDao.findById(hero.getId())).willReturn(hero);
        userService.removeHeroFromUser(70L, hero.getId());
        assertEquals(user1.getHeroes().size(), 0);
    }

    @Test(expectedExceptions = DnDServiceException.class)
    public void removeHeroWhenUserIdAndHeroIdIsWrong(){
        userService.removeHeroFromUser(45L, 61L);
        assertEquals(user1.getHeroes().size(), 0);
    }
}