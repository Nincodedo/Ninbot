package com.nincraft.ninbot.components.reaction;

import com.nincraft.ninbot.components.common.GenericDao;
import lombok.extern.log4j.Log4j2;
import org.hibernate.SessionFactory;
import org.springframework.stereotype.Repository;

@Log4j2
@Repository
public class ReactionDao extends GenericDao<Reaction> {

    public ReactionDao(SessionFactory sessionFactory) {
        super(sessionFactory);
    }
}
