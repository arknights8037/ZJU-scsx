<template>
    <Header/>
    <el-card >
        <template #header>
            <div class="card-header">
                <span>收货信息</span>
            </div>
        </template>
        <el-form :model="data.form" label-width="auto" style="max-width: 600px">
            <el-form-item label="收货人">
                <el-input v-model="data.form.recName" />
            </el-form-item>
            <el-form-item label="收货地址">
                <el-input v-model="data.form.recAddress" />
            </el-form-item>
            <el-form-item label="联系方式">
                <el-input v-model="data.form.recPhone" />
            </el-form-item>
        </el-form>
    </el-card>
    <el-card >
        <template #header>
            <div class="card-header">
                <span>订单详情</span>
            </div>
        </template>
        <el-table :data="data.form.records" stripe style="width: 100%" @selection-change="handleSelectionChange">
            <el-table-column  width="180" >
                <template #default="scope">
                    <img    v-if="scope.row.goods.goodsPicture"
                            :src="`http://localhost:8080/${scope.row.goods.goodsPicture}`" width="100" height="100"/>
                    <img    v-else :src="defaultImg" width="80" height="80"/>
                </template>
            </el-table-column>
            <el-table-column prop="goods.goodsName" label="商品名称" width="600" />
            <el-table-column prop="store.storeName" label="门店名称" width="300"/>
            <el-table-column label="价格" width="200">
                <template #default="scope">
                    <span style="color: #f56c6c; font-weight: bold;">¥{{scope.row.goodsStore.goodsPrice.toFixed(2)}}</span>
                </template>
            </el-table-column>
            <el-table-column prop="amount" label="数量"/>
        </el-table>
    </el-card>
    <el-card>
        <p>商品金额:¥{{sum.toFixed(2)}}</p>
        <p>运费:¥{{fee.toFixed(2)}}</p>
        <p v-if="user.userStatus===2">PLUS 95折:¥{{data.form.totalPrice.toFixed(2)}}</p>
        <p>合计:¥{{data.form.totalPrice.toFixed(2)}}</p>
        <template #footer>Footer content</template>
    </el-card>
</template>

<script setup>
    import Header from '../header/Header.vue'
    import {useRouter} from 'vue-router'
    import {reactive,ref} from 'vue'
    import defaultImg from '@/assets/no.png'
    const data = reactive({
            form: {
                userId: '',       //用户ID
                records: [],      //购买的商品
                recName: '',      //收货人
                recAddress: '',  //收货地址
                recPhone: '',    //联系电话
                totalPrice: 0   //订单价格
            }
    })
    //商品金额
    const sum = ref(0)
    //运费
    const fee = ref(0)
    //取出sessionStorage中存入的用户对象
    const user = ref(JSON.parse(sessionStorage.getItem("user")))
    data.form.userId = user.value.id

    data.form.records = JSON.parse(sessionStorage.getItem("carts"))

    //data.form.records = JSON.parse(route.query.records)
    console.log(data.form)
    console.log(user)

    //计算订单价格
    const computerSum = ()=>{
        //计算商品金额
        data.form.records.forEach(item=>{//如果有选择，遍历选中项，计算总价
            sum.value += item.goodsStore.goodsPrice * item.amount
        })
        //计算运费
        if(user.userStatus===2) fee.value = 0 //如果是会员免运费
        else{
            if(sum.value>=59) fee.value = 0
            else fee.value = 6
        }
        data.form.totalPrice = sum.value + fee.value
        //计算订单价格
        //会员打95折
        if(user.value.userStatus===2) data.form.totalPrice = sum.value * 0.95


    }

    computerSum()
</script>

<style scoped>

</style>