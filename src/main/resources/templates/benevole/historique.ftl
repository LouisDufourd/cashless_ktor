<#import "template.ftl" as layout />
<@layout.mainLayout>
<!doctype html>
<html lang="fr">
<header class="masthead">

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
                    <p class="text-black-50 mb-0">Vous pourrez consultez ici les d'historique de vente du stand</p>
                </div>
            </div>
        </div>
        <div class="row gx-0 mb-5 mb-lg-0 justify-content-center">
            <div class="col-lg-6">
                <div class="bg-black text-center h-100 project">
                    <div class="d-flex h-100">
                        <div class="project-text w-100 my-auto text-center text-lg-left">
                            <h4 class="text-white">Historique</h4>
                            <table>
                                <#list historique as hts>
                                    <tr>
                                        <td class="align-middle col-md-3">${hts.horodatage}</td>
                                        <td class="align-middle col-md-3">${hts.carte.codeNFC}</td>
                                        <td class="align-middle col-md-3">${hts.argent}€</td>
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
<footer class="footer bg-black small text-center text-white-50">
    <div class="container px-4 px-lg-5">festival 2023</div>
</footer>
</@layout.mainLayout>
