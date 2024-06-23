package server.server.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import server.server.models.Deliverer;
import server.server.models.DelivererOffer;
import server.server.models.Order;
import server.server.models.User;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public interface DelivererOffersRepository extends JpaRepository<DelivererOffer, Long> {
    List<DelivererOffer> getDelivererOfferByOrder_BuyerOrderByDateTimeDesc(User buyer);

    DelivererOffer getByOrder(Order order);

    List<DelivererOffer> getDelivererOfferByDelivererAndStatus_OfferStatusId(Deliverer deliverer, Long id);

    List<DelivererOffer> getDelivererOfferByDateTimeBetweenAndStatus_OfferStatusIdAndDeliverer(LocalDateTime start, LocalDateTime end, Long id, Deliverer deliverer);

    List<DelivererOffer> getDelivererOfferByOrder_IdAndStatus_OfferStatusId(Long orderId, Long offerStatusId);

    DelivererOffer getDelivererOfferByDeliverer_IdAndOrder_Id(Long delivererId, Long orderId);

    List<DelivererOffer> getDelivererOffersByOrder_Id(Long orderId);

}
