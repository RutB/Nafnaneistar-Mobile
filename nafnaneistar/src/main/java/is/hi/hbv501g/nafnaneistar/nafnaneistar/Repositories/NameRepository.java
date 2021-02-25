package is.hi.hbv501g.nafnaneistar.nafnaneistar.Repositories;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import is.hi.hbv501g.nafnaneistar.nafnaneistar.Entities.NameCard;

public interface NameRepository extends JpaRepository<NameCard, Long> {
    NameCard save(NameCard namecard);

    void delete(NameCard namecard);

    List<NameCard> findAll();

    Optional<NameCard> findById(Integer id);
    List<NameCard> findAllByDescriptionLike(String s); //breyta í name like? 
    List<NameCard> findAllByGender(boolean bool);
    List<NameCard> findAllByNameLike(String s);
    List<NameCard> findAllByNameLikeAndGender(String s, Boolean b); // HAT, test f. namesearch
    Optional<NameCard> findDescriptionByName(String s);
    Integer countByGender(boolean gender);

}
