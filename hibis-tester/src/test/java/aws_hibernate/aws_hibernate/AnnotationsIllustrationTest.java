/*
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * Copyright (c) 2010, Red Hat Inc. or third-party contributors as
 * indicated by the @author tags or express copyright attribution
 * statements applied by the authors.  All third-party contributions are
 * distributed under license by Red Hat Inc.
 *
 * This copyrighted material is made available to anyone wishing to use, modify,
 * copy, or redistribute it subject to the terms and conditions of the GNU
 * Lesser General Public License, as published by the Free Software Foundation.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY
 * or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Lesser General Public License
 * for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this distribution; if not, write to:
 * Free Software Foundation, Inc.
 * 51 Franklin Street, Fifth Floor
 * Boston, MA  02110-1301  USA
 */
package aws_hibernate.aws_hibernate;

import java.util.Date;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.boot.MetadataSources;
import org.hibernate.boot.registry.StandardServiceRegistry;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;

import junit.framework.TestCase;

/**
 * Illustrates the use of Hibernate native APIs. The code here is unchanged from
 * the {@code basic} example, the only difference being the use of annotations
 * to supply the metadata instead of Hibernate mapping files.
 *
 * @author Steve Ebersole
 */
public class AnnotationsIllustrationTest extends TestCase {
	private SessionFactory sessionFactory;

	@Override
	protected void setUp() throws Exception {
		// A SessionFactory is set up once for an application!
		final StandardServiceRegistry registry = new StandardServiceRegistryBuilder().configure() // configures
																									// settings
																									// from
																									// hibernate.cfg.xml
				.build();
		try {
			sessionFactory = new MetadataSources(registry).buildMetadata().buildSessionFactory();
		} catch (Exception e) {
			// The registry would be destroyed by the SessionFactory, but we had
			// trouble building the SessionFactory
			// so destroy it manually.
			System.err.println(e.getMessage());
			StandardServiceRegistryBuilder.destroy(registry);
		}
	}

	@Override
	protected void tearDown() throws Exception {
		if (sessionFactory != null) {
			sessionFactory.close();
		}
	}

	@SuppressWarnings({ "unchecked" })
	public void testBasicUsage() {
		Session session = sessionFactory.openSession();
//		StringBuilder sb = new StringBuilder();
//		for (int i = 0; i < 10; i++) {
//			sb.append(i);
//			sb.append(":LOB  tester!");
//		}
		
		for (int i = 0; i < 50000; i++) {
			session.beginTransaction();
			Event e = new Event("Our " + i + "st event!", new Date(), ThreadLocalRandom.current().nextDouble(-180, 180),
					ThreadLocalRandom.current().nextDouble(-90, 90));
			System.out.println("Testi:" + e.getLongitude());
			System.out.println("Testi:" + e.getLatitude());
			session.save(e);
			session.getTransaction().commit();	
		}
		
		session.close();
		// now lets pull events from the database and list them
		session = sessionFactory.openSession();
		List result = session.createQuery("from Event").list();
		for (Event event : (List<Event>) result) {
			System.out.println("Event (" + event.getDate() + ") : " + event.getTitle() + " : Longitude:"
					+ event.getLongitude() + " : Latitude:" + event.getLatitude());
		}
		session.close();
	}
}
