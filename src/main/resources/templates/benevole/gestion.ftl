<#import "template.ftl" as layout />
<@layout.mainLayout>
<!doctype html>
<html lang="fr">
<header class="masthead">
    <link rel="stylesheet" href="/static/css/modifierPopup.css">
    <div class="container px-4 px-lg-5 d-flex h-100 align-items-center justify-content-center">
        <div class="d-flex justify-content-center">
            <div class="text-center">
                <h1 class="mx-auto my-0 text-uppercase">Festival</h1>
                <h2 class="text-white-50 mx-auto mt-2 mb-5">Du 20 au 23 juillet 2023</h2>
            </div>
        </div>
    </div>
</header>
<section class="projects-section bg-light" id="projects">
    <div class="container px-4 px-lg-5">
        <div class="row gx-0 mb-4 mb-lg-5 align-items-center">
            <div class="col-xl-4 col-lg-5">
                <div class="featured-text text-center text-lg-left">
                    <p class="text-black-50 mb-0">Vous pourrez consultez ici l'interface de gestion du stand</p>
                    <!--c'est l'interface de gestion-->
                </div>
            </div>
        </div>
        <div class="row gx-0 mb-5 mb-lg-0 justify-content-center">
            <div class="col-lg-6">
                <div class="bg-black text-center h-100 project">
                    <div class="d-flex h-100">
                        <button name="Ajouter" id="buttoon" class="open-button" onclick="openForm()"><strong>Ajouter</strong></button>
                            <div class="login-popup">
                                <div class="form-popup" id="popupForm">
                                    <form action="/ajouter" class="form-container" type="GET">
                                        <button type="button" class="btn cancel buttoon" onclick="closeForm()">Fermer</button>
                                    </form>
                                </div>
                            </div>
                            <script>
                                function openForm() {
                                    document.getElementById("popupForm").style.display = "block";
                                }

                                function closeForm() {
                                    document.getElementById("popupForm").style.display = "none";
                                }
                            </script>
                        <div class="project-text w-100 my-auto text-center text-lg-left">
                            <h4 class="text-white">Gestion</h4>
                            <table>
                                <#list gestion as gest>
                                    <tr>
                                        <td class="align-middle col-md-3">
                                            ${gest.article.produit}
                                        </td>
                                        <td class="align-middle col-md-3 ${gest?index}">
                                            ${gest.stand.idStand}
                                        </td>
                                        <td class="align-middle col-md-3 ${gest?index}">
                                            ${gest.article.idArticle}
                                        </td>
                                        <td class="align-middle col-md-3 ${gest?index}">
                                            ${gest.prix}€
                                        </td>
                                        <td class="align-middle col-md-3 ${gest?index}">
                                            ${gest.quantite}
                                        </td>
                                        <td>
                                            <div class="open-btn align-middle col-md-3">
                                                <button class="open-button open buttoon"><strong>Modifier</strong></button>
                                            </div>
                                        </td>
                                    </tr>
                                </#list>
                            </table>
                            <hr class="d-none d-lg-block mb-0 ms-0"/>
                        </div>
                    </div>
                </div>
            </div>
        </div>
    </div>
</section>
<div class="modal" id="popup">
    <div class="modal-back"></div>
    <div class="modal-container">
        <form method="get" action="10.0.0.111:8080/modifier">
            <label for="idStand">idStand </label><input type="text" name="idStand" value="" id="idStand" disabled/><br>
            <label for="idArticle">idArticle </label><input type="text" name="idArticle" value="" id="idArticle" disabled/><br>
            <label for="price">Prix </label><input type="number" name="price" value="" step="0.01" id="price" min="0"/><br>
            <label for="quantity">Quantité </label><input type="number" value="" name="quantity" id="quantity" min="0"/><br>
            <button type="submit">Valider</button>
            <button type="reset" id="close">Annuler</button>
        </form>
    </div>
</div>
<script src="/static/js/modifierPopup.js"></script>
<footer class="footer bg-black small text-center text-white-50">
    <div class="container px-4 px-lg-5">festival 2023</div>
</footer>
</@layout.mainLayout>
